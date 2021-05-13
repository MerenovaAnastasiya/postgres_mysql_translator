package merenaas.com.postgres_translator.connector.service;

import java.sql.Connection;

public interface ConnectionService {

    Connection getConnection();
    void closeConnection(Connection connection);

}
