package merenaas.com.postgres_translator.connector.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface SchemaInformationService {
    Collection<String> getSchemaTableNames(String schemaName);
}
