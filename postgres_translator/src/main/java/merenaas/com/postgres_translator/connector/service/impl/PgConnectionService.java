package merenaas.com.postgres_translator.connector.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgres_translator.connector.configuration.DatabaseConfiguration;
import merenaas.com.postgres_translator.connector.service.ConnectionService;
import org.postgresql.PGConnection;
import org.postgresql.PGProperty;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Service
@Slf4j
@RequiredArgsConstructor
public class PgConnectionService implements ConnectionService {

    private static final String DRIVER_CLASSNAME = "org.postgresql.Driver";
    private final DataSource dataSource;
    private final DatabaseConfiguration databaseConfiguration;


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

    public PGConnection getReplicationConnection() {
        try {
            Class.forName(DRIVER_CLASSNAME);
            Properties props = new Properties();
            PGProperty.USER.set(props, databaseConfiguration.getUsername());
            PGProperty.PASSWORD.set(props, databaseConfiguration.getPassword());
            PGProperty.ASSUME_MIN_SERVER_VERSION.set(props, "9.4");
            PGProperty.REPLICATION.set(props, "database");
            PGProperty.PREFER_QUERY_MODE.set(props, "simple");
            var connection = DriverManager.getConnection(databaseConfiguration.getJdbcUrl(), props);
            return connection.unwrap(PGConnection.class);
        } catch (SQLException exception) {
            throw new RuntimeException("Error when trying to create connection");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Error when trying to get class by className = %s", DRIVER_CLASSNAME));
        }
    }

    public void setAutoCommit(boolean autoCommit) {
//        try {
//            connection.setAutoCommit(autoCommit);
//        } catch (SQLException exception) {
//            throw new RuntimeException("Error when trying changeAutoCommit");
//        }
    }

}
