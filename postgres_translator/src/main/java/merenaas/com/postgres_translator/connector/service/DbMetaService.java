package merenaas.com.postgres_translator.connector.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public interface DbMetaService {

    Set<String> getDatabaseSchemaNames(Connection connection);
}
