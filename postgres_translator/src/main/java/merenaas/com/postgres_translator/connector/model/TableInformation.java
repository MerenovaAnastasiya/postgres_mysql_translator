package merenaas.com.postgres_translator.connector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

@Data
public class TableInformation {

    private final TableName tableName;
    private final Set<ColumnInformation> columnsInformation;
    private final PrimaryKeyInfo primaryKeyInfo;

}
