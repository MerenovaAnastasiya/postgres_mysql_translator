package merenaas.com.postgres_translator.connector.service.impl;

import lombok.RequiredArgsConstructor;
import merenaas.com.postgres_translator.connector.service.MySQLTranslator;
import merenaas.com.postgres_translator.connector.service.PgReplicationService;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PgMySqlDecoderReplicationService implements PgReplicationService {

    private final MySQLTranslator mySQLTranslator;
    private static final String PLUGIN_NAME = "pg_mysql_decoder";

    @Override
    public void createLogicalReplicationSlot(PgConnection connection, String slotName) {
        try {
            connection.getReplicationAPI()
                    .createReplicationSlot()
                    .logical()
                    .withSlotName(slotName)
                    .withOutputPlugin(PLUGIN_NAME)
                    .make();
        } catch (SQLException e) {
            throw new RuntimeException("Error when trying create logical slot");
        }
    }

    @Override
    public PGReplicationStream createLogicalReplicationStream(PgConnection connection, String slotName, Properties slotOptions) {
        try {
            return connection.getReplicationAPI()
                    .replicationStream()
                    .logical()
                    .withSlotName(slotName)
                    .withStartPosition(LogSequenceNumber.valueOf("0/1D75858"))
                    .withSlotOptions(slotOptions)
                    .withStatusInterval(20, TimeUnit.SECONDS)
                    .start();
        } catch (SQLException e) {
            throw new RuntimeException("xxx");
        }
    }

    @Override
    public Optional<String> read(PGReplicationStream pgReplicationStream) {
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

    @Override
    public void replicateData(PGReplicationStream pgReplicationStream, Connection mySqlConnection) {
        while (true) {
            var dataOptional = read(pgReplicationStream);
            if (dataOptional.isPresent()) {
                var data = dataOptional.get();
                mySQLTranslator.executeQuery(mySqlConnection, data);
                var lastLsn = pgReplicationStream.getLastReceiveLSN();
                pgReplicationStream.setAppliedLSN(pgReplicationStream.getLastReceiveLSN());
                pgReplicationStream.setFlushedLSN(pgReplicationStream.getLastReceiveLSN());
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
