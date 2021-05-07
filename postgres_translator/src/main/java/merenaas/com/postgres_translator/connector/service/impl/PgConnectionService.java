package merenaas.com.postgres_translator.connector.service.impl;


import lombok.RequiredArgsConstructor;
import merenaas.com.postgres_translator.connector.model.DatabaseCredentials;
import merenaas.com.postgres_translator.connector.service.ConnectionService;
import org.postgresql.PGProperty;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class PgConnectionService implements ConnectionService {

    private final DatabaseCredentials databaseCredentials;
    private Connection connection;
    private static final String DRIVER_CLASSNAME = "org.postgresql.Driver";

    @Override
    public Connection createConnection(DatabaseCredentials databaseCredentials) {
        try {
            Class.forName(DRIVER_CLASSNAME);
            Properties props = new Properties();
            PGProperty.USER.set(props, databaseCredentials.getUser());
            PGProperty.PASSWORD.set(props, databaseCredentials.getPassword());
            PGProperty.ASSUME_MIN_SERVER_VERSION.set(props, "9.4");
            PGProperty.REPLICATION.set(props, "database");
            PGProperty.PREFER_QUERY_MODE.set(props, "simple");
            return DriverManager.getConnection(databaseCredentials.getUrl(), props);
        } catch (SQLException exception) {
            throw new RuntimeException("Error when trying to create connection");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Error when trying to get class by className = %s", DRIVER_CLASSNAME));
        }
    }

    @Override
    public Connection getConnection() {
        if (this.connection == null) {
            connection = createConnection(databaseCredentials);
        }
        return this.connection;
    }

    public void setAutoCommit(boolean autoCommit) {
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException exception) {
            throw new RuntimeException("Error when trying changeAutoCommit");
        }
    }

    public <T> T unwrap(Connection connection, Class<T> cl) {
        try {
            return connection.unwrap(cl);
        } catch (SQLException e) {
            throw new RuntimeException("Error when trying wrap connection");
        }
    }

}
