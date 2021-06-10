package merenaas.com.postgresql_translator.mysql_consumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColumnInformation {

    private String columnName;
    private String columnType;
    private Boolean isNullable;
    private Integer characterMaximumLength;
    private String columnDefault;
    private Integer numericPrecision;

}
