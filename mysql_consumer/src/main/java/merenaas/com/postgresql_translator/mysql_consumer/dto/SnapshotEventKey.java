package merenaas.com.postgresql_translator.mysql_consumer.dto;

import lombok.Value;

@Value
public class SnapshotEventKey {
    String url;
    String database;
    String schema;
    String table;
}
