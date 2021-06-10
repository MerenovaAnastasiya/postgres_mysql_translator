package merenaas.com.postgresql_translator.mysql_consumer.service;

import java.sql.Connection;

public interface ConnectionService {

    Connection getConnection();
    void closeConnection(Connection connection);

}
