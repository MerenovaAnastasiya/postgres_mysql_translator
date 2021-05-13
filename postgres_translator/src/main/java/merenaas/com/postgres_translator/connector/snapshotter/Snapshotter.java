package merenaas.com.postgres_translator.connector.snapshotter;


import java.sql.SQLException;

public interface Snapshotter {
    void makeSnapshot() throws SQLException;
}
