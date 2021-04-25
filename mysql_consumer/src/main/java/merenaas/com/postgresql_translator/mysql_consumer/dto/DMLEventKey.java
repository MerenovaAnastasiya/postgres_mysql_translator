package merenaas.com.postgresql_translator.mysql_consumer.dto;

import lombok.Value;

@Value
public class DMLEventKey {
    String url;
    String database;
}
