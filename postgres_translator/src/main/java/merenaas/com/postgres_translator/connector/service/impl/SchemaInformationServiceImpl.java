package merenaas.com.postgres_translator.connector.service.impl;

import lombok.RequiredArgsConstructor;
import merenaas.com.postgres_translator.connector.service.ConnectionService;
import merenaas.com.postgres_translator.connector.service.SchemaInformationService;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SchemaInformationServiceImpl implements SchemaInformationService {

    private final ConnectionService connectionService;

    public Collection<String> getSchemaTableNames(String schemaName) {
        var connection = connectionService.getConnection();
        Set<String> tableNames = new HashSet<>();
        try {
            String sql = "SELECT table_name FROM information_schema.tables  WHERE table_schema= ? ORDER BY table_name;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, schemaName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String tableName = resultSet.getString("table_name");
                tableNames.add(tableName);
            }
            return tableNames;
        } catch (SQLException ex) {
            throw new RuntimeException("Error when trying to consume a snapshot");
        }
    }
}
