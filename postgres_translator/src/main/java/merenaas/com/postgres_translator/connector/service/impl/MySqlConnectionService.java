package merenaas.com.postgres_translator.connector.service.impl;

import merenaas.com.postgres_translator.connector.model.DatabaseConfiguration;
import merenaas.com.postgres_translator.connector.service.ConnectionService;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class MySqlConnectionService implements ConnectionService {
    @Override
    public Connection createConnection(DatabaseConfiguration databaseConfiguration) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(databaseConfiguration.getUrl(), databaseConfiguration.getUser(), databaseConfiguration.getPassword());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
