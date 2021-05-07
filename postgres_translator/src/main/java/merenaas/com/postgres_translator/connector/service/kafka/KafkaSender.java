package merenaas.com.postgres_translator.connector.service.kafka;

import lombok.RequiredArgsConstructor;
import merenaas.com.postgres_translator.connector.model.TableInformation;
import merenaas.com.postgres_translator.connector.model.kafka.CreateSchemaEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
@RequiredArgsConstructor
public class KafkaSender {

    private final KafkaTemplate<String, String> snapshotKafkaTemplate;

    public ListenableFuture<SendResult<String, String>> sendCreateSchemaEvent(CreateSchemaEvent event) {
        return null;
//        return snapshotKafkaTemplate.sendDefault(event);
    }

    public ListenableFuture<SendResult<String, String>> sendCreateTableEvent(TableInformation informationAboutTable) {
//        snapshotKafkaTemplate.sendDefault();
        return null;
    }
}
