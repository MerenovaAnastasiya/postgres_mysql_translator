package merenaas.com.postgres_translator.connector.model;

import lombok.Getter;

import java.util.Set;


@Getter
public class PgTableInformation extends TableInformation<PgColumnInformation> {

    public PgTableInformation(TableName tableName, Set<PgColumnInformation> columnsInformation, PrimaryKeyInfo primaryKeyInfo) {
        super(tableName, columnsInformation, primaryKeyInfo);
    }
}
