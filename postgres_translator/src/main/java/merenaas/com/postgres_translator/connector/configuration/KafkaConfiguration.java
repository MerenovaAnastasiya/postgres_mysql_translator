package merenaas.com.postgres_translator.connector.configuration;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Bean
    public KafkaTemplate<String, String> snapshotKafkaTemplate(ProducerFactory<String, String> snapshotKafkaProducerFactory,
                                                                                 TopicInformation snapshotTopicInformation) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(snapshotKafkaProducerFactory);
        kafkaTemplate.setDefaultTopic(snapshotTopicInformation.getName());
        return kafkaTemplate;
    }

    @Bean
    public ProducerFactory<String, String> snapshotKafkaProducerFactory(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties, TopicInformation snapshotTopicInformation) {
        return kafkaProducerFactory(defaultKafkaProperties, snapshotTopicInformation, StringSerializer.class, StringSerializer.class);
    }

    @Bean
    @ConfigurationProperties("kafka.topic.snapshot")
    public TopicInformation snapshotTopicInformation() {
        return new TopicInformation();
    }

    @Bean
    @ConfigurationProperties(value = "kafka.default")
    public KafkaProperties defaultKafkaProperties() {
        return new KafkaProperties();
    }

    private <K, V, TK, TV> ProducerFactory<TK, TV> kafkaProducerFactory(KafkaProperties kafkaProperties,
                                                                        TopicInformation topicInformation,
                                                                        Class<K> keySerializerClass,
                                                                        Class<V> valueSerializerClass) {
        Map<String, Object> configuration = new HashMap<>(kafkaProperties.getProducer());
        configuration.putAll(topicInformation.getProducer());
        configuration.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getUrl());
        configuration.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
        configuration.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);
        return new DefaultKafkaProducerFactory<>(configuration);
    }

    @Getter
    @Setter
    private static class KafkaProperties {
        private final Map<String, String> consumer = new HashMap<>();
        private final Map<String, String> producer = new HashMap<>();
        private String url;
    }

    @Setter
    @Getter
    static class TopicInformation {
        private final Map<String, String> consumer = new HashMap<>();
        private final Map<String, String> producer = new HashMap<>();
        private String name;
    }

}
