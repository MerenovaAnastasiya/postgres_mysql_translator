package merenaas.com.postgresql_translator.mysql_consumer.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgresql_translator.mysql_consumer.model.SchemaInformation;
import merenaas.com.postgresql_translator.mysql_consumer.model.TableInformation;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConfiguration {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SchemaInformation> createSchemaListenerContainer(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties,
                                                                                                            TopicInformation createSchemaTopicInformation,
                                                                                                            ErrorHandler errorHandler) {
        var container = new ConcurrentKafkaListenerContainerFactory<String, SchemaInformation>();
//        container.setErrorHandler(errorHandler);
        var config = kafkaConfigs(defaultKafkaProperties, createSchemaTopicInformation, StringDeserializer.class, JsonDeserializer.class, SchemaInformation.class);
        var containerFactory = new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(SchemaInformation.class));
        container.setConsumerFactory(containerFactory);
        return container;
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TableInformation> createTableListenerContainer(@Qualifier("defaultKafkaProperties")
                                                                                                          KafkaProperties defaultKafkaProperties,
                                                                                                          TopicInformation createTableTopicInformation,
                                                                                                          ErrorHandler errorHandler) {
        var container = new ConcurrentKafkaListenerContainerFactory<String, TableInformation>();
        container.setErrorHandler(errorHandler);
        var config = kafkaConfigs(defaultKafkaProperties, createTableTopicInformation, StringDeserializer.class, JsonDeserializer.class, TableInformation.class);
        var containerFactory = new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(TableInformation.class));
        container.setConsumerFactory(containerFactory);
        return container;
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> testSchemaListenerContainer(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties,
                                                                                               TopicInformation testSchemaTopicInformation,
                                                                                               ErrorHandler errorHandler) {
        var container = new ConcurrentKafkaListenerContainerFactory<String, String>();
        var config = kafkaConfigs(defaultKafkaProperties, testSchemaTopicInformation, StringDeserializer.class, StringDeserializer.class, null);
        container.setErrorHandler(errorHandler);
        var containerFactory = new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new StringDeserializer());
        container.setConsumerFactory(containerFactory);
        return container;
    }

    private <K, V, T> Map<String, Object> kafkaConfigs(KafkaProperties kafkaProperties, TopicInformation topicInformation,
                                                       Class<K> keyDeserializerClass,
                                                       Class<V> valueDeserializerClass,
                                                       @Nullable Class<T> defaultType) {
        Map<String, Object> configs = new HashMap<>(kafkaProperties.getConsumer());
        configs.putAll(topicInformation.getConsumer());
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getUrl());
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClass);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass);
        configs.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, keyDeserializerClass);
        configs.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, valueDeserializerClass.getName());
        Optional.ofNullable(defaultType).ifPresent(type -> configs.put(JsonDeserializer.VALUE_DEFAULT_TYPE, type));
        return configs;
    }

    @Bean
    @ConfigurationProperties("kafka.topic.create-schema")
    public TopicInformation createSchemaTopicInformation() {
        return new TopicInformation();
    }

    @Bean
    @ConfigurationProperties("kafka.topic.create-table")
    public TopicInformation createTableTopicInformation() {
        return new TopicInformation();
    }

    @Bean
    @ConfigurationProperties("kafka.topic.test")
    public TopicInformation testSchemaTopicInformation() {
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

    @Bean
    public ErrorHandler errorHandler() {
        return (exception, data) -> log.error("Error in process with Exception {} and the record is {}", exception, data);
    }
}
