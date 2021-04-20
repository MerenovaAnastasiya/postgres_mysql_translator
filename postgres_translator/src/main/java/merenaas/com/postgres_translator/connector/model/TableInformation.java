package merenaas.com.postgres_translator.connector.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

@Getter
@EqualsAndHashCode
public abstract class TableInformation<T> {

    protected final TableName tableName;
    protected final Set<T> columnsInformation;
    protected final PrimaryKeyInfo primaryKeyInfo;

    public TableInformation(TableName tableName, Set<T> columnsInformation, PrimaryKeyInfo primaryKeyInfo) {
        this.tableName = tableName;
        this.columnsInformation = columnsInformation;
        this.primaryKeyInfo = primaryKeyInfo;
    }

}
