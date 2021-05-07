package merenaas.com.postgresql_translator.mysql_consumer.connection;

import java.sql.Connection;

public interface ConnectionHolder {

    Connection getConnection();

}
