package merenaas.com.postgresql_translator.mysql_consumer.dto;

import lombok.Value;

@Value
public class SchemasSnapshotEventValue {
    SnapshotEventType eventType;

}
