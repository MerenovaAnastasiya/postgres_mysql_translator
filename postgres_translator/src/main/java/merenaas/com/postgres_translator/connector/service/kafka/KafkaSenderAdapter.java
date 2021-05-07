package merenaas.com.postgres_translator.connector.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgres_translator.connector.builder.CreateSchemaEventBuilder;
import merenaas.com.postgres_translator.connector.model.TableInformation;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaSenderAdapter {

    private final CreateSchemaEventBuilder createSchemaEventBuilder;
    private final KafkaSender kafkaSender;

    public void sendSyncCreateSchemaEvent(String schemaName) {
        try {
            var kafkaCreateSchemaEvent = createSchemaEventBuilder.build(schemaName);
            kafkaSender.sendCreateSchemaEvent(kafkaCreateSchemaEvent).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to send information about creating schema with name = {}", schemaName);
        } catch (ExecutionException e) {
            log.error("Failed to send information about creating schema with name = {}", schemaName);
        }
    }

    public void sendAsyncCreateTableEvent(TableInformation informationAboutTable) {
//        try {
            kafkaSender.sendCreateTableEvent(informationAboutTable);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            log.error("Failed to send information about creating table with name = {} and schema_name = {}", informationAboutTable.getTableName().getName(), informationAboutTable.getTableName().getSchemaName());
//        } catch (ExecutionException e) {
//            log.error("Failed to send information about creating table with name = {} and schema_name = {}", informationAboutTable.getTableName().getName(), informationAboutTable.getTableName().getSchemaName());
//        }
    }

    public void sendDMlEvent(String schemaName, String sql) {

    }
}
