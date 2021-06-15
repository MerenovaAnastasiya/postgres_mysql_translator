package merenaas.com.postgres_translator.connector.service;

import merenaas.com.postgres_translator.connector.model.PagingEntity;
import merenaas.com.postgres_translator.connector.model.TableInformation;
import merenaas.com.postgres_translator.connector.model.TableName;
import merenaas.com.postgres_translator.connector.model.TableRow;

public interface TableService {

    void shareLock(TableName tableName);
    TableInformation getColumnsInformationAboutTable(TableName tableName);
    PagingEntity<TableRow> selectFromTable(TableName tableName, Integer limit, Integer offset);
}
