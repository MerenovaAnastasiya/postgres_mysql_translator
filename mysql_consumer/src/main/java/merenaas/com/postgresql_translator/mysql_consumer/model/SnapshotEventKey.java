package merenaas.com.postgresql_translator.mysql_consumer.model;

import lombok.Value;

@Value
public class SnapshotEventKey {
    String url;
    String database;
    String schema;
    String table;
}
