package merenaas.com.postgresql_translator.mysql_consumer.service;

import merenaas.com.postgresql_translator.mysql_consumer.model.DatabaseCredentials;

import java.sql.Connection;

public interface ConnectionService {

    Connection createConnection(DatabaseCredentials databaseCredentials);

}
