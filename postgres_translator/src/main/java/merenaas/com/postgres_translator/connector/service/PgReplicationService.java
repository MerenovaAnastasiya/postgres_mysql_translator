package merenaas.com.postgres_translator.connector.service;

import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.PGReplicationStream;

import java.sql.Connection;
import java.util.Optional;
import java.util.Properties;

public interface PgReplicationService {

    void createLogicalReplicationSlot(PgConnection connection, String slotName);

    PGReplicationStream createLogicalReplicationStream(PgConnection connection, String slotName, Properties options);

    Optional<String> read(PGReplicationStream pgReplicationStream);

    void replicateData(PGReplicationStream pgReplicationStream, Connection mySqlConnection);
}
