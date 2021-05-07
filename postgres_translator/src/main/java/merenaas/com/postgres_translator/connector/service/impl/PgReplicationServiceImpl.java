package merenaas.com.postgres_translator.connector.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgres_translator.connector.service.WalJournalService;
import merenaas.com.postgres_translator.connector.service.kafka.KafkaSenderAdapter;
import merenaas.com.postgres_translator.connector.service.replication.PgReplicationService;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${replication.lsn-table-name}")
    private String lsnTableName;

    @Override
    public void createLogicalReplicationSlot(String slotName, String pluginName) {
        var connection = connectionService.getConnection();
        var replicationConnection = connectionService.unwrap(connection, PgConnection.class);
        try {
            replicationConnection.getReplicationAPI()
                    .createReplicationSlot()
                    .logical()
                    .withSlotName(slotName)
                    .withOutputPlugin(pluginName)
                    .make();
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Error when trying create logical slot with name = %s and plugin = %s", slotName, pluginName));
        }
    }

    @Override
    public void replicateData(String slotName, String schemaName, @Nullable Properties slotOptions) {
        var connection = connectionService.getConnection();
        var replicationConnection = connectionService.unwrap(connection, PgConnection.class);
        var lastLsn = findLastLsn(slotName);
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

                }
                var lastLsnSeqNumber = pgReplicationStream.getLastReceiveLSN();
                pgReplicationStream.setAppliedLSN(lastLsnSeqNumber);
                pgReplicationStream.setFlushedLSN(lastLsnSeqNumber);
                //todo подключить пул соединений, в одном потоке не обрабатывается
                //всегда сохраняем ласт_лсн на случа падения
//                saveLastLsn(slotName, lastLsnSeqNumber.asString());
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private PGReplicationStream createLogicalReplicationStream(PgConnection connection, @Nullable Properties slotOptions, @Nullable String startPosition, String slotName) {
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

    private void saveLastLsn(String slotName, String lastLsn) {
        var connection = connectionService.getConnection();
        var sql = "INSERT INTO " + lsnTableName + "(lsn, slot_name, time) VALUES(?, ?, now())";
        try {
            var statement = connection.prepareStatement(sql);
            statement.setString(1, lastLsn);
            statement.setString(2, slotName);
            statement.execute();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    private Optional<String> findLastLsn(String slotName) {
        var connection = connectionService.getConnection();
        var sql = "SELECT lsn FROM " + lsnTableName + " WHERE slot_name = ? ORDER BY time DESC LIMIT 1";
        try {
            var statement = connection.prepareStatement(sql);
            statement.setString(1, slotName);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.ofNullable(resultSet.getString("lsn"));
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.warn("SQL exception when trying to get last lsn");
            return Optional.empty();
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
