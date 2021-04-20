package merenaas.com.postgres_translator.connector.service;

import merenaas.com.postgres_translator.connector.model.PgTableInformation;
import merenaas.com.postgres_translator.connector.model.TableName;

import java.sql.Connection;

public interface DbTableService {

    void shareLock(Connection connection, TableName tableName);
    PgTableInformation getColumnsInformationAboutTable(Connection connection, TableName tableName);
}
