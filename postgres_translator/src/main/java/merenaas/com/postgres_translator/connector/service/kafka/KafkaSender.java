package merenaas.com.postgres_translator.connector.service.kafka;

import lombok.RequiredArgsConstructor;
import merenaas.com.postgres_translator.connector.model.SchemaInformation;
import merenaas.com.postgres_translator.connector.model.TableInformation;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
@RequiredArgsConstructor
public class KafkaSender {

    private final KafkaTemplate<String, SchemaInformation> createSchemaKafkaTemplate;
    private final  KafkaTemplate<String, TableInformation> createTableKafkaTemplate;
    private final KafkaTemplate<String, String> dmlOperationKafkaTemplate;

    public ListenableFuture<SendResult<String, SchemaInformation>> sendCreateSchemaEvent(SchemaInformation schemaInformation) {
       return createSchemaKafkaTemplate.sendDefault(schemaInformation.getSchemaName(), schemaInformation);
    }

    public ListenableFuture<SendResult<String, TableInformation>> sendCreateTableEvent(TableInformation informationAboutTable) {
        return createTableKafkaTemplate.sendDefault(informationAboutTable.getTableName().getName(), informationAboutTable);
    }

    //нет default топика, тк название топика определеяется исходя из содержимого запроса
    public ListenableFuture<SendResult<String, String>> sendDmlOperationEvent(String schemaName, String tableName, String dmlOperation) {
        return dmlOperationKafkaTemplate.send(schemaName, tableName, dmlOperation);
    }
}
