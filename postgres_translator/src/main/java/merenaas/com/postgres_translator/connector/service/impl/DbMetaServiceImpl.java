package merenaas.com.postgres_translator.connector.service.impl;

import lombok.RequiredArgsConstructor;
import merenaas.com.postgres_translator.connector.service.DbMetaService;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DbMetaServiceImpl implements DbMetaService {

    private final Set<String> pgServiceSchemaNames;

    @Override
    public Set<String> getDatabaseSchemaNames(Connection connection) {
        var in = pgServiceSchemaNames.stream()
                .map(str -> "'" + str + "'")
                .collect(Collectors.joining(",", "(", ")"));
        var sql = "SELECT schema_name FROM information_schema.schemata WHERE schema_name NOT IN(?)";
        Set<String> schemaNames = new HashSet<>();
        try {
            var statement = connection.prepareStatement(sql.replace("(?)", in));
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                schemaNames.add(resultSet.getString("schema_name"));
            }
            return schemaNames;
        }
        catch (SQLException ex) {
            throw new RuntimeException("Error when trying get all schemas");
        }

    }

}
