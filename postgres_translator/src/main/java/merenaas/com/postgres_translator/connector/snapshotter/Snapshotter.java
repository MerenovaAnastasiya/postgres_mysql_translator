package merenaas.com.postgres_translator.connector.snapshotter;

import merenaas.com.postgres_translator.connector.model.DatabaseConfiguration;

import java.sql.Connection;

public interface Snapshotter {
    void makeSnapshot(DatabaseConfiguration pgConfiguration, DatabaseConfiguration mySqlConfiguration, String replicationSlotName);
}
