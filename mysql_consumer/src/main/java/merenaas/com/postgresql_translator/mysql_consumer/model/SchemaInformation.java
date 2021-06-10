package merenaas.com.postgresql_translator.mysql_consumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemaInformation {

    private String schemaName;
    private String comment;
}
