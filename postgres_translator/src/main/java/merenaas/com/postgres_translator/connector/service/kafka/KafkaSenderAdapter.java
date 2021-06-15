package merenaas.com.postgres_translator.connector.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgres_translator.connector.model.TableInformation;
import merenaas.com.postgres_translator.connector.model.TableName;
import merenaas.com.postgres_translator.connector.service.SQLGeneratorService;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaSenderAdapter {

    private final KafkaSender kafkaSender;
    private final SQLGeneratorService sqlGeneratorService;

    public void sendSyncCreateTableEvent(TableInformation informationAboutTable) {
        try {
            var sql = generateCreateTableSql(informationAboutTable);
            kafkaSender.sendSqlOperationEvent(informationAboutTable.getTableName().getSchemaName(), informationAboutTable.getTableName().getSchemaName(), sql).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to send information about creating table with name = {}", informationAboutTable.getTableName());
        } catch (ExecutionException e) {
            log.error("Failed to send information about creating table with name = {}", informationAboutTable.getTableName());
        }
    }

    private String generateCreateTableSql(TableInformation informationAboutTable) {
        return sqlGeneratorService.generateCreateTableSQL(informationAboutTable);
    }

    public void sendAsyncDMlEvent(TableName tableName, String dmlOperation) {
        sendAsync(() -> kafkaSender.sendSqlOperationEvent(tableName.getSchemaName(), tableName.getName(), dmlOperation));
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
