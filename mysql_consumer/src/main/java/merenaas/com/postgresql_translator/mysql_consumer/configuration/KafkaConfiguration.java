package merenaas.com.postgresql_translator.mysql_consumer.configuration;

import lombok.Getter;
import lombok.Setter;
import merenaas.com.postgresql_translator.mysql_consumer.model.DMLEventKey;
import merenaas.com.postgresql_translator.mysql_consumer.model.SnapshotEventKey;
import merenaas.com.postgresql_translator.mysql_consumer.model.SchemasSnapshotEventValue;
import merenaas.com.postgresql_translator.mysql_consumer.model.TablesSnapshotEventValue;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<SnapshotEventKey, SchemasSnapshotEventValue> schemasSnapshotListenerContainer(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties,
                                                                                                                                 TopicInformation schemasSnapshotTopicInformation) {
        ConcurrentKafkaListenerContainerFactory<SnapshotEventKey, SchemasSnapshotEventValue> container = new ConcurrentKafkaListenerContainerFactory<>();
        container.setConsumerFactory(kafkaConsumerFactory(defaultKafkaProperties, schemasSnapshotTopicInformation, JsonDeserializer.class, JsonDeserializer.class));
        return container;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<SnapshotEventKey, TablesSnapshotEventValue> tablesSnapshotListenerContainer(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties,
                                                                                                                               TopicInformation tablesSnapshotTopicInformation) {
        ConcurrentKafkaListenerContainerFactory<SnapshotEventKey, TablesSnapshotEventValue> container = new ConcurrentKafkaListenerContainerFactory<>();
        container.setConsumerFactory(kafkaConsumerFactory(defaultKafkaProperties, tablesSnapshotTopicInformation, JsonDeserializer.class, JsonDeserializer.class));
        return container;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<DMLEventKey, String> insertListenerContainer(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties,
                                                                                             TopicInformation insertTopicInformation) {
        ConcurrentKafkaListenerContainerFactory<DMLEventKey, String> container = new ConcurrentKafkaListenerContainerFactory<>();
        container.setConsumerFactory(kafkaConsumerFactory(defaultKafkaProperties, insertTopicInformation, JsonDeserializer.class, StringDeserializer.class));
        return container;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<DMLEventKey, String> deleteListenerContainer(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties,
                                                                                           TopicInformation deleteTopicInformation) {
        ConcurrentKafkaListenerContainerFactory<DMLEventKey, String> container = new ConcurrentKafkaListenerContainerFactory<>();
        container.setConsumerFactory(kafkaConsumerFactory(defaultKafkaProperties, deleteTopicInformation, JsonDeserializer.class, StringDeserializer.class));
        return container;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<DMLEventKey, String> updateListenerContainer(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties,
                                                                                           TopicInformation updateTopicInformation) {
        ConcurrentKafkaListenerContainerFactory<DMLEventKey, String> container = new ConcurrentKafkaListenerContainerFactory<>();
        container.setConsumerFactory(kafkaConsumerFactory(defaultKafkaProperties, updateTopicInformation, JsonDeserializer.class, StringDeserializer.class));
        return container;
    }

    private <TK, TV, K extends Deserializer<TK>, V extends Deserializer<TV>> ConsumerFactory<TK, TV> kafkaConsumerFactory(KafkaProperties kafkaProperties,
                                                                                                                          TopicInformation topicInformation,
                                                                                                                          Class<K> keyDeserializerClass,
                                                                                                                          Class<V> valueDeserializerClass) {
        Map<String, Object> configs = new HashMap<>(kafkaProperties.getConsumer());
        configs.putAll(topicInformation.getConsumer());
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getUrl());
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClass);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass);
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    @ConfigurationProperties("kafka.topic.schemas-snapshot")
    public TopicInformation schemasSnapshotTopicInformation() {
        return new TopicInformation();
    }

    @Bean
    @ConfigurationProperties("kafka.topic.tables-snapshot")
    public TopicInformation tablesSnapshotTopicInformation() {
        return new TopicInformation();
    }

    @Bean
    @ConfigurationProperties("kafka.topic.insert")
    public TopicInformation insertTopicInformation() {
        return new TopicInformation();
    }

    @Bean
    @ConfigurationProperties("kafka.topic.delete")
    public TopicInformation deleteTopicInformation() {
        return new TopicInformation();
    }

    @Bean
    @ConfigurationProperties("kafka.topic.update")
    public TopicInformation updateTopicInformation() {
        return new TopicInformation();
    }

    @Bean
    @ConfigurationProperties(value = "kafka.default")
    public KafkaProperties defaultKafkaProperties() {
        return new KafkaProperties();
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
