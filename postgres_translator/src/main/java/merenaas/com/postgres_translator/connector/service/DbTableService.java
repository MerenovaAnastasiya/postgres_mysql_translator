package merenaas.com.postgres_translator.connector.service;

import merenaas.com.postgres_translator.connector.model.TableInformation;
import merenaas.com.postgres_translator.connector.model.TableName;

public interface DbTableService {

    void shareLock(TableName tableName);
    TableInformation getColumnsInformationAboutTable(TableName tableName);
}
