package merenaas.com.postgres_translator.connector.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
public class PrimaryKeyInfo {

    private final String constraintName;
    private final List<String> columnNames;

}


