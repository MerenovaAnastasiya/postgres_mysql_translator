package merenaas.com.postgres_translator.connector.service;

public interface SQLGeneratorService {

    String createSchema(String schemaName);
    String createTable();
}
