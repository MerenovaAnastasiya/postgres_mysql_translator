package merenaas.com.postgres_translator.connector.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgres_translator.connector.service.ConnectionService;
import merenaas.com.postgres_translator.connector.model.ColumnInformation;
import merenaas.com.postgres_translator.connector.model.PrimaryKeyInfo;
import merenaas.com.postgres_translator.connector.model.TableInformation;
import merenaas.com.postgres_translator.connector.model.TableName;
import merenaas.com.postgres_translator.connector.service.DbTableService;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DbTableServiceImpl implements DbTableService {

    private final ConnectionService connectionService;

    @Override
    public void shareLock(TableName tableName) {
        try {
            var connection = connectionService.getConnection();
            String sql = "LOCK TABLE " + tableName.getSchemaName() + "." + tableName.getName() + " IN ACCESS SHARE MODE;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.execute();
        } catch (SQLException e) {
            log.warn("Error when trying lock table with name = {} and schema = {}", tableName.getName(), tableName.getSchemaName());
        }
    }

    @Override
    public TableInformation getColumnsInformationAboutTable(TableName tableName) {
        var connection = connectionService.getConnection();
        var sql = "SELECT column_name, data_type, is_nullable, data_type," +
                " character_maximum_length, column_default, numeric_precision" +
                " FROM information_schema.columns WHERE table_name = ? AND table_schema = ?;";
        try {
            var statement = connection.prepareStatement(sql);
            statement.setString(1, tableName.getName());
            statement.setString(2, tableName.getSchemaName());
            var resultSet = statement.executeQuery();
            Set<ColumnInformation> allColumnInformation = new HashSet<>();
            while (resultSet.next()) {
                boolean isNullable = resultSet.getString("is_nullable").equals("YES");
                ColumnInformation columnInformation = ColumnInformation.builder()
                        .columnName(resultSet.getString("column_name"))
                        .columnType(resultSet.getString("data_type").toUpperCase())
                        .characterMaximumLength(resultSet.getObject("character_maximum_length", Integer.class))
                        .columnDefault(resultSet.getString("column_default"))
                        .numericPrecision(resultSet.getObject("numeric_precision", Integer.class))
                        .isNullable(isNullable)
                        .build();
                allColumnInformation.add(columnInformation);
            }
            var primaryKeyInfo = getPrimaryKeyInfo(tableName);
            return new TableInformation(tableName, allColumnInformation, primaryKeyInfo);
        } catch (SQLException exception) {
            throw new RuntimeException("Error when trying get information about table");
        }
    }

    private PrimaryKeyInfo getPrimaryKeyInfo(TableName tableName) {
        var connection = connectionService.getConnection();
        var sql = "SELECT tco.constraint_name, kcu.ordinal_position AS position,  " +
                "kcu.column_name AS key_column " +
                "FROM information_schema.table_constraints tco " +
                "JOIN information_schema.key_column_usage kcu " +
                "ON kcu.constraint_name = tco.constraint_name " +
                "AND kcu.constraint_schema = tco.constraint_schema " +
                "AND kcu.constraint_name = tco.constraint_name " +
                "WHERE tco.constraint_type = 'PRIMARY KEY' AND kcu.table_schema = ? AND kcu.table_name = ? " +
                "ORDER BY kcu.table_schema," +
                "kcu.table_name, position;";
        try {
            var statement = connection.prepareStatement(sql);
            statement.setString(1, tableName.getSchemaName());
            statement.setString(2, tableName.getName());
            var resultSet = statement.executeQuery();
            List<String> columnNames = new ArrayList<>(10);
            var constraintName = "";
            while (resultSet.next()) {
                columnNames.add(resultSet.getString("key_column"));
                if (resultSet.isLast()) {
                    constraintName = resultSet.getString("constraint_name");
                }
            }
            if(columnNames.isEmpty()) {
                throw new IllegalArgumentException("Impossible to take a snapshot of the table without PK!");
            }
            return PrimaryKeyInfo.builder()
                    .columnNames(columnNames)
                    .constraintName(constraintName)
                    .build();
        } catch (SQLException ex) {
            throw new RuntimeException("Error when trying get primary key");
        }
    }
}
