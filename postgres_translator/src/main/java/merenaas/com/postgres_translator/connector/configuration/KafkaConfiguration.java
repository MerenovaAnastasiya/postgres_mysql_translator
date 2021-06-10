package merenaas.com.postgres_translator.connector.configuration;

import lombok.Getter;
import lombok.Setter;
import merenaas.com.postgres_translator.connector.model.SchemaInformation;
import merenaas.com.postgres_translator.connector.model.TableInformation;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

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
    @ConfigurationProperties("kafka.topic.dml-operation")
    public TopicInformation dmlOperationTopicInformation() {
        return new TopicInformation();
    }

    @Bean
    public KafkaTemplate<String, SchemaInformation> createSchemaKafkaTemplate(ProducerFactory<String, SchemaInformation> createSchemaKafkaProducerFactory,
                                                                              TopicInformation createSchemaTopicInformation) {
        KafkaTemplate<String, SchemaInformation> kafkaTemplate = new KafkaTemplate<>(createSchemaKafkaProducerFactory);
        kafkaTemplate.setDefaultTopic(createSchemaTopicInformation.getName());
        return kafkaTemplate;
    }

    @Bean
    public KafkaTemplate<String, TableInformation> createTableKafkaTemplate(ProducerFactory<String, TableInformation> createTableKafkaProducerFactory,
                                                                              TopicInformation createTableTopicInformation) {
        KafkaTemplate<String, TableInformation> kafkaTemplate = new KafkaTemplate<>(createTableKafkaProducerFactory);
        kafkaTemplate.setDefaultTopic(createTableTopicInformation.getName());
        return kafkaTemplate;
    }

    @Bean
    public KafkaTemplate<String, String> dmlOperationKafkaTemplate(ProducerFactory<String, String> dmlOperationKafkaProducerFactory) {
        return new KafkaTemplate<>(dmlOperationKafkaProducerFactory);
    }

    @Bean
    public ProducerFactory<String, SchemaInformation> createSchemaKafkaProducerFactory(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties,
                                                                                      TopicInformation createSchemaTopicInformation) {
        return new DefaultKafkaProducerFactory<>(kafkaProducerConfiguration(defaultKafkaProperties, createSchemaTopicInformation,
                StringSerializer.class, JsonSerializer.class));
    }

    @Bean
    public ProducerFactory<String, TableInformation> createTableKafkaProducerFactory(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties,
                                                                                     TopicInformation createTableTopicInformation) {
        return new DefaultKafkaProducerFactory<>(kafkaProducerConfiguration(defaultKafkaProperties, createTableTopicInformation,
                StringSerializer.class, JsonSerializer.class));
    }

    @Bean
    public ProducerFactory<String, String> dmlOperationKafkaProducerFactory(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaProducerConfiguration(defaultKafkaProperties,
                StringSerializer.class, StringSerializer.class));
    }


    @Bean
    @ConfigurationProperties(value = "kafka.default")
    public KafkaProperties defaultKafkaProperties() {
        return new KafkaProperties();
    }


    private <K, V> Map<String, Object> kafkaProducerConfiguration(KafkaProperties kafkaProperties,
                                                                  TopicInformation topicInformation,
                                                                  Class<K> keySerializerClass,
                                                                  Class<V> valueSerializerClass) {
        Map<String, Object> configuration = new HashMap<>(kafkaProperties.getProducer());
        configuration.putAll(topicInformation.getProducer());
        configuration.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getUrl());
        configuration.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
        configuration.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);
        configuration.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return configuration;
    }

    private <K, V> Map<String, Object> kafkaProducerConfiguration(KafkaProperties kafkaProperties,
                                                                  Class<K> keySerializerClass,
                                                                  Class<V> valueSerializerClass) {
        Map<String, Object> configuration = new HashMap<>(kafkaProperties.getProducer());
        configuration.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getUrl());
        configuration.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
        configuration.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);
        return configuration;
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
