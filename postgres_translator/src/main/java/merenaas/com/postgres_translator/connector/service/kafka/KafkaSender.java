package merenaas.com.postgres_translator.connector.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
@RequiredArgsConstructor
public class KafkaSender {

    private final KafkaTemplate<String, String> sqlOperationKafkaTemplate;

    //нет default топика, тк название топика определеяется исходя из содержимого запроса
    public ListenableFuture<SendResult<String, String>> sendSqlOperationEvent(String schemaName, String tableName, String sql) {
        return sqlOperationKafkaTemplate.send(schemaName, tableName, sql);
    }
}
