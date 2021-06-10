package merenaas.com.postgresql_translator.mysql_consumer.controller;

import lombok.RequiredArgsConstructor;
import merenaas.com.postgresql_translator.mysql_consumer.model.SchemaInformation;
import merenaas.com.postgresql_translator.mysql_consumer.model.TableInformation;
import merenaas.com.postgresql_translator.mysql_consumer.service.DDLOperationService;
import merenaas.com.postgresql_translator.mysql_consumer.service.DMLOperationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaController {

    private static final String CREATE_SCHEMA_EVENT_CONTAINER_ID = "createSchemaEventContainer";
    private static final String CREATE_TABLE_EVENT_CONTAINER_ID = "createTableEventContainer";
    private static final String TEST_SCHEMA_EVENT_CONTAINER_ID = "testSchemaEventContainer";

    private final DDLOperationService ddlOperationService;
    private final DMLOperationService dmlOperationService;


    @KafkaListener(id = CREATE_SCHEMA_EVENT_CONTAINER_ID,
            topics = "${kafka.topic.create-schema.name:create-schema}",
            containerFactory = "createSchemaListenerContainer",
            concurrency = "${kafka.topic.create-schema.concurrency:1}",
            clientIdPrefix = "#{T(java.util.UUID).randomUUID().toString()}",
            idIsGroup = false)
    public void handleCreateSchemaEvent(SchemaInformation schemaInformation) {
        ddlOperationService.createSchema(schemaInformation);
    }

    @KafkaListener(id = CREATE_TABLE_EVENT_CONTAINER_ID,
            topics = "${kafka.topic.create-table.name:create-table}",
            containerFactory = "createTableListenerContainer",
            concurrency = "${kafka.topic.create-table.concurrency:1}",
            clientIdPrefix = "#{T(java.util.UUID).randomUUID().toString()}",
            idIsGroup = false)
    public void handleCreateTableEvent(TableInformation tableInformation) {
        ddlOperationService.createTable(tableInformation);
    }

    @KafkaListener(id = TEST_SCHEMA_EVENT_CONTAINER_ID,
            topics = "${kafka.topic.test.name:test}",
            containerFactory = "testSchemaListenerContainer",
            concurrency = "${kafka.topic.test.concurrency:1}",
            clientIdPrefix = "#{T(java.util.UUID).randomUUID().toString()}",
            idIsGroup = false)
    public void handleDmlOperationEvent(String query) {
        dmlOperationService.executeQuery(query);
    }

}
