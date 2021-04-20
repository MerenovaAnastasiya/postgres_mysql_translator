package merenaas.com.postgres_translator.connector.service.impl;

import merenaas.com.postgres_translator.connector.service.SQLGeneratorService;
import org.springframework.stereotype.Service;

@Service
public class SQLGeneratorServiceImpl implements SQLGeneratorService {

    @Override
    public String createSchema(String schemaName) {
        return String.format("CREATE schema %s;", schemaName);
    }

    @Override
    public String createTable() {
        return null;
    }
}
