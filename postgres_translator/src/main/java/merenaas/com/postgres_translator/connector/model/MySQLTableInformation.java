package merenaas.com.postgres_translator.connector.model;

import lombok.Getter;

import java.util.Set;

@Getter
public class MySQLTableInformation extends TableInformation<MySQLColumnInformation> {

    public MySQLTableInformation(TableName tableName, Set<MySQLColumnInformation> columnsInformation, PrimaryKeyInfo primaryKeyInfo) {
        super(tableName, columnsInformation, primaryKeyInfo);
    }
}
