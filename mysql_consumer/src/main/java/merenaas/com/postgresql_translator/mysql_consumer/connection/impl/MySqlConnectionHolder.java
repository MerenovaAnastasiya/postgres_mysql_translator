package merenaas.com.postgresql_translator.mysql_consumer.connection.impl;

import lombok.RequiredArgsConstructor;
import merenaas.com.postgresql_translator.mysql_consumer.model.DatabaseCredentials;
import merenaas.com.postgresql_translator.mysql_consumer.connection.ConnectionHolder;
import merenaas.com.postgresql_translator.mysql_consumer.service.ConnectionService;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Component
@RequiredArgsConstructor
public class MySqlConnectionHolder implements ConnectionHolder {

    private final DatabaseCredentials databaseCredentials;
    private final ConnectionService connectionService;
    private Connection connection;

    @Override
    public Connection getConnection() {
        if (this.connection == null) {
            connection = connectionService.createConnection(databaseCredentials);
        }
        return this.connection;
    }

//    @Bean
//    @ConfigurationProperties(value = "db")
//    public DatabaseConfiguration databaseConfiguration() {
//        return new DatabaseConfiguration();
//    }
}
