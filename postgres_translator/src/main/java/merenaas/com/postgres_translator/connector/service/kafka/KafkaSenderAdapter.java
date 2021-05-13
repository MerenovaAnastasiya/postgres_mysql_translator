package merenaas.com.postgres_translator.connector.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgres_translator.connector.builder.CreateSchemaEventBuilder;
import merenaas.com.postgres_translator.connector.model.SchemaInformation;
import merenaas.com.postgres_translator.connector.model.TableInformation;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaSenderAdapter {

    private final CreateSchemaEventBuilder createSchemaEventBuilder;
    private final KafkaSender kafkaSender;

    public void sendSyncCreateSchemaEvent(SchemaInformation schemaInformation) {
        try {
            kafkaSender.sendCreateSchemaEvent(schemaInformation).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to send information about creating schema with name = {}", schemaInformation.getSchemaName());
        } catch (ExecutionException e) {
            log.error("Failed to send information about creating schema with name = {}", schemaInformation.getSchemaName());
        }
    }

    public void sendAsyncCreateTableEvent(TableInformation informationAboutTable) {
        sendAsync(() -> kafkaSender.sendCreateTableEvent(informationAboutTable));
    }

    public void sendAsyncDMlEvent(String schemaName, String tableName,  String dmlOperation) {
        sendAsync(() -> kafkaSender.sendDmlOperationEvent(schemaName, tableName, dmlOperation));
    }


    private void sendAsync(Supplier<ListenableFuture<?>> supplierSend) {
        var copyOfContextMap = MDC.getCopyOfContextMap();
        supplierSend.get().addCallback(
                success -> {
                },
                error -> {
                    MDC.setContextMap(copyOfContextMap);
                    log.error("Error when trying to send message into kafka");
                });
    }
}
