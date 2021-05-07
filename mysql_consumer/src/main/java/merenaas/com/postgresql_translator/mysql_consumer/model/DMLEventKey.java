package merenaas.com.postgresql_translator.mysql_consumer.model;

import lombok.Value;

@Value
public class DMLEventKey {
    String url;
    String database;
}
