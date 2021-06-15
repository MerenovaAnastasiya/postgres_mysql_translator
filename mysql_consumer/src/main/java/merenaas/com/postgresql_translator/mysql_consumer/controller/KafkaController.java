package merenaas.com.postgresql_translator.mysql_consumer.controller;

import lombok.RequiredArgsConstructor;
import merenaas.com.postgresql_translator.mysql_consumer.service.SqlOperationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaController {

    private static final String SQL_OPERATION_CONTAINER_ID = "sqlOperationContainer";

    private final SqlOperationService sqlOperationService;

    @KafkaListener(id = SQL_OPERATION_CONTAINER_ID,
            topics = "${replication.include-schemas}",
            containerFactory = "sqlOperationListenerContainer",
            concurrency = "${kafka.topic.test.concurrency:1}",
            clientIdPrefix = "#{T(java.util.UUID).randomUUID().toString()}",
            idIsGroup = false
    )
    public void handleSqlQuery(String query) {
        //не указываем здесь ключ топика, тк это имя таблицы и оно есть в самом запросе(пока что этот парам. излишен)
        sqlOperationService.executeQuery(query);
    }

}
