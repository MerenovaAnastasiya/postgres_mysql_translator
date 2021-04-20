package merenaas.com.postgres_translator.connector.service;

import merenaas.com.postgres_translator.connector.model.MySQLTableInformation;

import java.sql.Connection;

public interface MySQLTranslator {

    void createSchema(Connection connection, String schemaName);
    void createTable(Connection connection, MySQLTableInformation tableInformation);
    void executeQuery(Connection connection, String query);
}
