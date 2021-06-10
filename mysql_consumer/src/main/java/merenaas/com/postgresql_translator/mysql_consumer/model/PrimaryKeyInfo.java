package merenaas.com.postgresql_translator.mysql_consumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrimaryKeyInfo {

    private String constraintName;
    private List<String> columnNames;

}


