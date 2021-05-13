package merenaas.com.postgres_translator.connector.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchemaInformation {

    private String schemaName;
    private String comment;
}
