package merenaas.com.postgres_translator.connector.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ColumnInformation {

    private final String columnName;
    private final String columnType;
    private final Boolean isNullable;
    private final Integer characterMaximumLength;
    private final String columnDefault;
    private final Integer numericPrecision;

}
