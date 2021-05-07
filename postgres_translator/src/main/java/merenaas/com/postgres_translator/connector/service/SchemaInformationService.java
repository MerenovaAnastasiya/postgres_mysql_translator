package merenaas.com.postgres_translator.connector.service;

import java.util.Collection;

public interface SchemaInformationService {

    Collection<String> getSchemaTableNames(String schemaName);
}
