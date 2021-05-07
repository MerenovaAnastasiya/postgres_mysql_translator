package merenaas.com.postgresql_translator.mysql_consumer.service.impl;

import merenaas.com.postgresql_translator.mysql_consumer.model.DatabaseCredentials;
import merenaas.com.postgresql_translator.mysql_consumer.service.ConnectionService;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class MySqlConnectionService implements ConnectionService {

    @Override
    public Connection createConnection(DatabaseCredentials databaseCredentials) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(databaseCredentials.getUrl(), databaseCredentials.getUser(), databaseCredentials.getPassword());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
