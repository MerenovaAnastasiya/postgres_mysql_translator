package merenaas.com.postgresql_translator.mysql_consumer.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgresql_translator.mysql_consumer.service.ConnectionService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MySqlConnectionService implements ConnectionService {

    private final DataSource dataSource;

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void closeConnection(Connection connection) {
        try {
            connection.close();
        }
        catch (SQLException ex) {
            log.error("Error when trying close connection");
        }
    }
}
