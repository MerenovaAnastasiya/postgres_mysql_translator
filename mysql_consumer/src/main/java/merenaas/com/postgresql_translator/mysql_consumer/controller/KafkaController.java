package merenaas.com.postgresql_translator.mysql_consumer.controller;

import merenaas.com.postgresql_translator.mysql_consumer.model.SchemasSnapshotEventValue;
import merenaas.com.postgresql_translator.mysql_consumer.model.TablesSnapshotEventValue;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaController {

    private static final String TABLES_SNAPSHOT_EVENT_CONTAINER_ID = "tablesSnapshotEventContainer";
    private static final String SCHEMAS_SNAPSHOT_EVENT_CONTAINER_ID = "schemasSnapshotEventContainer";
    private static final String INSERT_EVENT_CONTAINER_ID = "insertEventContainer";
    private static final String DELETE_EVENT_CONTAINER_ID = "deleteEventContainer";
    private static final String UPDATE_EVENT_CONTAINER_ID = "updateEventContainer";

    @KafkaListener(id = TABLES_SNAPSHOT_EVENT_CONTAINER_ID,
            topics = "${kafka.topic.tables-snapshot.name:tables-snapshot}",
            containerFactory = "tablesSnapshotListenerContainer",
            concurrency = "${kafka.topic.tables_snapshot.concurrency:1}",
            clientIdPrefix = "#{T(java.util.UUID).randomUUID().toString()}",
            idIsGroup = false,
            autoStartup = "false")
    public void handleTablesSnapshotEvent(TablesSnapshotEventValue eventValue) {

    }

    @KafkaListener(id = SCHEMAS_SNAPSHOT_EVENT_CONTAINER_ID,
            topics = "${kafka.topic.schemas-snapshot.name:schemas-snapshot}",
            containerFactory = "schemasSnapshotListenerContainer",
            concurrency = "${kafka.topic.schemas_snapshot.concurrency:1}",
            clientIdPrefix = "#{T(java.util.UUID).randomUUID().toString()}",
            idIsGroup = false)
    public void handleSchemasSnapshotEvent(SchemasSnapshotEventValue event) {

    }

    @KafkaListener(id = INSERT_EVENT_CONTAINER_ID,
            topics = "${kafka.topic.insert.name:insert}",
            containerFactory = "insertListenerContainer",
            concurrency = "${kafka.topic.snapshot.concurrency:1}",
            clientIdPrefix = "#{T(java.util.UUID).randomUUID().toString()}",
            idIsGroup = false,
            autoStartup = "false")
    public void handleInsertEvent(String data) {

    }

    @KafkaListener(id = DELETE_EVENT_CONTAINER_ID,
            topics = "${kafka.topic.delete.name:delete}",
            containerFactory = "deleteListenerContainer",
            concurrency = "${kafka.topic.snapshot.concurrency:1}",
            clientIdPrefix = "#{T(java.util.UUID).randomUUID().toString()}",
            idIsGroup = false,
            autoStartup = "false")
    public void handleDeleteEvent(String data) {

    }

    @KafkaListener(id = UPDATE_EVENT_CONTAINER_ID,
            topics = "${kafka.topic.update.name:update}",
            containerFactory = "updateListenerContainer",
            concurrency = "${kafka.topic.snapshot.concurrency:1}",
            clientIdPrefix = "#{T(java.util.UUID).randomUUID().toString()}",
            idIsGroup = false,
            autoStartup = "false")
    public void handleUpdateEvent(String data) {

    }

}
