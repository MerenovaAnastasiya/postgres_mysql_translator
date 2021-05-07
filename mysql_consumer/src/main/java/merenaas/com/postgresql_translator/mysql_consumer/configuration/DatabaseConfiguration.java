package merenaas.com.postgresql_translator.mysql_consumer.configuration;

import merenaas.com.postgresql_translator.mysql_consumer.model.DatabaseCredentials;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {

    @Bean
    @ConfigurationProperties(value = "db")
    public DatabaseCredentials databaseConfiguration() {
        return new DatabaseCredentials();
    }

}
