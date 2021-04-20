package merenaas.com.postgres_translator.connector.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MySQLColumnInformation {

    private final String columnName;
    private final String columnType;
    private final Boolean isNullable;
    private final String columnDefault;
    private final Integer characterMaximumLength;
    private final Integer numericPrecision;

}
