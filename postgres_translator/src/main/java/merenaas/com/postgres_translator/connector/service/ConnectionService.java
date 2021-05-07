package merenaas.com.postgres_translator.connector.service;

import merenaas.com.postgres_translator.connector.model.DatabaseCredentials;

import java.sql.Connection;

public interface ConnectionService {

    Connection createConnection(DatabaseCredentials databaseCredentials);
    Connection getConnection();

}
