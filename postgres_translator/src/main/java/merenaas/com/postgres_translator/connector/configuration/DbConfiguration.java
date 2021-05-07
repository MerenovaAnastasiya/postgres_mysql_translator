package merenaas.com.postgres_translator.connector.configuration;

import merenaas.com.postgres_translator.connector.model.DatabaseCredentials;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbConfiguration {

    @Bean
    @ConfigurationProperties(value = "db")
    public DatabaseCredentials databaseConfiguration() {
        return new DatabaseCredentials();
    }

}
