package merenaas.com.postgres_translator.connector.model;

import lombok.Builder;
import lombok.Data;

import java.util.SortedMap;

@Data
@Builder
public class TableRow {

    private TableName tableName;
    private SortedMap<String, Object> columnMap;

}

