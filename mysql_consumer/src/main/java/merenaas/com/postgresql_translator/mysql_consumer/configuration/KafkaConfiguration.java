package merenaas.com.postgresql_translator.mysql_consumer.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
    public ConcurrentKafkaListenerContainerFactory<String, String> sqlOperationListenerContainer(@Qualifier("defaultKafkaProperties") KafkaProperties defaultKafkaProperties,
                                                                                                 ErrorHandler errorHandler) {
        var container = new ConcurrentKafkaListenerContainerFactory<String, String>();
        var config = kafkaConfigs(defaultKafkaProperties, StringDeserializer.class, StringDeserializer.class, null);
        container.setErrorHandler(errorHandler);
        var containerFactory = new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new StringDeserializer());
        container.setConsumerFactory(containerFactory);
        return container;
    }

    private <K, V, T> Map<String, Object> kafkaConfigs(KafkaProperties kafkaProperties,
                                                       Class<K> keyDeserializerClass,
                                                       Class<V> valueDeserializerClass,
                                                       @Nullable Class<T> defaultType) {
        Map<String, Object> configs = new HashMap<>(kafkaProperties.getConsumer());
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getUrl());
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClass);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass);
        configs.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, keyDeserializerClass);
        configs.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, valueDeserializerClass.getName());
        Optional.ofNullable(defaultType).ifPresent(type -> configs.put(JsonDeserializer.VALUE_DEFAULT_TYPE, type));
        return configs;
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

    @Bean
    public ErrorHandler errorHandler() {
        return (exception, data) -> log.error("Error in process with Exception {} and the record is {}", exception, data);
    }
}
