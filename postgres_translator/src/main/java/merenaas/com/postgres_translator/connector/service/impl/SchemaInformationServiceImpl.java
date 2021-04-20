package merenaas.com.postgres_translator.connector.service.impl;

import merenaas.com.postgres_translator.connector.service.SchemaInformationService;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class SchemaInformationServiceImpl implements SchemaInformationService {

    public Collection<String> getSchemaTableNames(Connection connection, String schemaName) {
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
