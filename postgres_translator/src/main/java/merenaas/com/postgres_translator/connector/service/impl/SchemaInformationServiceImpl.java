package merenaas.com.postgres_translator.connector.service.impl;

import lombok.RequiredArgsConstructor;
import merenaas.com.postgres_translator.connector.model.SchemaInformation;
import merenaas.com.postgres_translator.connector.service.ConnectionService;
import merenaas.com.postgres_translator.connector.service.SchemaInformationService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchemaInformationServiceImpl implements SchemaInformationService {

    private final ConnectionService connectionService;

    public Collection<String> getSchemaTableNames(String schemaName) {
        var connection = connectionService.getConnection();
        Set<String> tableNames = new HashSet<>();
        try {
            var sql = "SELECT table_name FROM information_schema.tables  WHERE table_schema= ? ORDER BY table_name;";
            var statement = connection.prepareStatement(sql);
            statement.setString(1, schemaName);
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                var tableName = resultSet.getString("table_name");
                tableNames.add(tableName);
            }
            return tableNames;
        } catch (SQLException ex) {
            throw new RuntimeException("Error when trying to consume a snapshot");
        }
        finally {
            connectionService.closeConnection(connection);
        }
    }

    public Map<String, SchemaInformation> getSchemaInfoByNames(Set<String> schemaNames) {
        var connection = connectionService.getConnection();
        Map<String, SchemaInformation> schemaInformationMap = new HashMap<>();
        try {
            var sql = String.format("SELECT obj_description(oid) as comment, s.nspname as schema_name " +
                    "FROM pg_catalog.pg_namespace s " +
                    "JOIN pg_catalog.pg_user u on u.usesysid = s.nspowner where oid=s.oid and nspname in (%s))", String.join(", ", schemaNames));
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                var schemaName = resultSet.getString("schema_name");
                var comment = resultSet.getString("comment");
                var schemaInfo = SchemaInformation.builder()
                        .schemaName(schemaName)
                        .comment(comment)
                        .build();
                schemaInformationMap.put(schemaName, schemaInfo);
            }
            return schemaInformationMap;
        } catch (SQLException e) {
            throw new RuntimeException("Error when trying to get schema info");
        } finally {
            connectionService.closeConnection(connection);
        }
    }
}
