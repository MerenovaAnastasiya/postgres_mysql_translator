package merenaas.com.postgres_translator.connector.connection;

import java.sql.Connection;

public interface ConnectionHolder {

    Connection getConnection();

}
