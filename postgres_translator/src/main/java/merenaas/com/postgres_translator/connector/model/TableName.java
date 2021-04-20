package merenaas.com.postgres_translator.connector.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TableName {

    protected final String name;
    protected final String schemaName;

}
