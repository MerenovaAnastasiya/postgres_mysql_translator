package merenaas.com.postgres_translator.connector.service;

import merenaas.com.postgres_translator.connector.model.DatabaseConfiguration;

import java.sql.Connection;

public interface ConnectionService {

    Connection createConnection(DatabaseConfiguration databaseConfiguration);
}
