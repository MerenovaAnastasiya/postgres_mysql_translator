package merenaas.com.postgres_translator.connector.service;

import java.sql.Connection;
import java.util.Collection;

public interface SchemaInformationService {

    Collection<String> getSchemaTableNames(Connection connection, String schemaName);

}
