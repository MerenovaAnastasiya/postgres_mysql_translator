package merenaas.com.postgres_translator.connector.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class ReplicationConfiguration {

    @ConfigurationProperties(prefix = "replication.include-schemas")
    @Bean
    public Set<String> replicationSchemas() {
        return new HashSet<>();
    }
}
