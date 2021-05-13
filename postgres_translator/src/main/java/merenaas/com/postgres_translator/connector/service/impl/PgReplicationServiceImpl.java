package merenaas.com.postgres_translator.connector.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgres_translator.connector.service.WalJournalService;
import merenaas.com.postgres_translator.connector.service.kafka.KafkaSenderAdapter;
import merenaas.com.postgres_translator.connector.service.replication.PgReplicationService;
import org.postgresql.PGConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class PgReplicationServiceImpl implements PgReplicationService {

    private final WalJournalService walJournalService;
    private final PgConnectionService connectionService;
    private final KafkaSenderAdapter kafkaSenderAdapter;

    @Override
    public void createLogicalReplicationSlot(String slotName, String pluginName) {
        var replicationConnection = connectionService.getReplicationConnection();
        try {
            replicationConnection.getReplicationAPI()
                    .createReplicationSlot()
                    .logical()
                    .withSlotName(slotName)
                    .withOutputPlugin(pluginName)
                    .make();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Error when trying create logical slot with name = %s and plugin = %s", slotName, pluginName));
        }
    }

    @Override
    public void replicateData(String slotName, String schemaName, @Nullable Properties slotOptions) {
        var replicationConnection = connectionService.getReplicationConnection();
        var lastLsn = walJournalService.findLastLsn(slotName);
        var startPosition = lastLsn.orElse(null);
        var replicationStream = createLogicalReplicationStream(replicationConnection, slotOptions, startPosition, slotName);
        replicateData(slotName, schemaName, replicationStream);
    }

    private void replicateData(String slotName, String schemaName, PGReplicationStream pgReplicationStream) {
        while (true) {
            var dataOptional = read(pgReplicationStream);
            if (dataOptional.isPresent()) {
                var data = dataOptional.get();
                if (queryForTargetScheme(data, schemaName)) {
                    getTableName(data).ifPresent(tableName -> kafkaSenderAdapter.sendAsyncDMlEvent(schemaName, tableName, data));
                    var lastLsnSeqNumber = pgReplicationStream.getLastReceiveLSN();
                    pgReplicationStream.setAppliedLSN(lastLsnSeqNumber);
                    pgReplicationStream.setFlushedLSN(lastLsnSeqNumber);
                    //всегда сохраняем ласт_лсн на случа падения
                    walJournalService.saveLastLsn(slotName, lastLsnSeqNumber.asString());
                }
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Optional<String> getTableName(String query) {
        var pattern = Pattern.compile("^(UPDATE|INSERT\\sINTO|DELETE\\sFROM)\\s(\\w+)\\.(\\w+)");
        var matcher = pattern.matcher(query);
        if (matcher.find()) {
            return Optional.ofNullable(matcher.group(3));
        }
        return Optional.empty();
    }

    private PGReplicationStream createLogicalReplicationStream(PGConnection connection, @Nullable Properties slotOptions, @Nullable String startPosition, String slotName) {
        try {
            var replicationStreamBuilder = connection.getReplicationAPI()
                    .replicationStream()
                    .logical()
                    .withSlotName(slotName)
                    .withStatusInterval(20, TimeUnit.SECONDS);
            if (startPosition != null) {
                replicationStreamBuilder
                        .withStartPosition(LogSequenceNumber.valueOf(startPosition));
            }
            Optional.ofNullable(slotOptions).ifPresent(replicationStreamBuilder::withSlotOptions);
            return replicationStreamBuilder.start();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Unable to create logical replication stream");
        }
    }

    private Optional<String> read(PGReplicationStream pgReplicationStream) {
        try {
            var byteBuffer = pgReplicationStream.readPending();
            if (byteBuffer == null) {
                return Optional.empty();
            } else {
                int offset = byteBuffer.arrayOffset();
                byte[] source = byteBuffer.array();
                int length = source.length - offset;
                return Optional.of(new String(source, offset, length));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private boolean queryForTargetScheme(String query, String schemaName) {
        var pattern = Pattern.compile("^(UPDATE|INSERT\\sINTO|DELETE\\sFROM)\\s(\\w+)\\.\\w+");
        var matcher = pattern.matcher(query);
        if (matcher.find()) {
            return Objects.equals(matcher.group(2), schemaName);
        }
        return false;
    }

}
