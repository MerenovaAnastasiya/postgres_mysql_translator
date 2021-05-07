package merenaas.com.postgresql_translator.mysql_consumer.model;

import lombok.Getter;

@Getter
public enum SnapshotEventType {
    CREATE_SCHEMA,
    CREATE_TABLE
}
