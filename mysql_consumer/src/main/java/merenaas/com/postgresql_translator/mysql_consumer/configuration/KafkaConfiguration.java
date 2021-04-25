package merenaas.com.postgresql_translator.mysql_consumer.configuration;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<byte[], byte[]> createSchemaListenerContainer(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties,
                                                                                             TopicInformation snapshotTopicInformation) {
        ConcurrentKafkaListenerContainerFactory<byte[], byte[]> container = new ConcurrentKafkaListenerContainerFactory<>();
        container.setConsumerFactory(kafkaConsumerFactory(defaultKafkaProperties, snapshotTopicInformation, ByteArrayDeserializer.class, ByteArrayDeserializer.class));
        return container;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<byte[], byte[]> insertListenerContainer(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties,
                                                                                           TopicInformation insertTopicInformation) {
        ConcurrentKafkaListenerContainerFactory<byte[], byte[]> container = new ConcurrentKafkaListenerContainerFactory<>();
        container.setConsumerFactory(kafkaConsumerFactory(defaultKafkaProperties, insertTopicInformation, ByteArrayDeserializer.class, ByteArrayDeserializer.class));
        return container;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<byte[], byte[]> deleteListenerContainer(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties,
                                                                                           TopicInformation deleteTopicInformation) {
        ConcurrentKafkaListenerContainerFactory<byte[], byte[]> container = new ConcurrentKafkaListenerContainerFactory<>();
        container.setConsumerFactory(kafkaConsumerFactory(defaultKafkaProperties, deleteTopicInformation, ByteArrayDeserializer.class, ByteArrayDeserializer.class));
        return container;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<byte[], byte[]> updateListenerContainer(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties,
                                                                                           TopicInformation updateTopicInformation) {
        ConcurrentKafkaListenerContainerFactory<byte[], byte[]> container = new ConcurrentKafkaListenerContainerFactory<>();
        container.setConsumerFactory(kafkaConsumerFactory(defaultKafkaProperties, updateTopicInformation, ByteArrayDeserializer.class, ByteArrayDeserializer.class));
        return container;
    }

    private <TK, TV, K extends Deserializer<TK>, V extends Deserializer<TV>> ConsumerFactory<TK, TV> kafkaConsumerFactory(KafkaProperties kafkaProperties,
                                                                                                                          TopicInformation topicInformation,
                                                                                                                          Class<K> keySerializerClass,
                                                                                                                          Class<V> valueSerializerClass) {
        Map<String, Object> configs = new HashMap<>(kafkaProperties.getConsumer());
        configs.putAll(topicInformation.getConsumer());
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getUrl());
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keySerializerClass);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueSerializerClass);
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    @ConfigurationProperties("kafka.topic.snapshot")
    public TopicInformation snapshotTopicInformation() {
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
