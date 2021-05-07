package merenaas.com.postgresql_translator.mysql_consumer.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgresql_translator.mysql_consumer.connection.ConnectionHolder;
import merenaas.com.postgresql_translator.mysql_consumer.model.TableInformation;
import merenaas.com.postgresql_translator.mysql_consumer.service.DDLOperationService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DDLOperationServiceImpl implements DDLOperationService {

    private final ConnectionHolder connectionHolder;

    @Override
    public void createSchema(String schemaName) {
        var connection = connectionHolder.getConnection();
        var sql = "CREATE SCHEMA IF NOT EXISTS " + schemaName + ";";
        try {
            var statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            throw new RuntimeException("");
        }
    }


    //todo доработать операцию
    @Override
    public void createTable(TableInformation tableInformation) {
        var connection = connectionHolder.getConnection();
        var tableName = tableInformation.getTableName().getSchemaName() + "." + tableInformation.getTableName().getName();
        var columns = tableInformation.getColumnsInformation().stream()
                .map(columnInfo -> {
                    var type = columnInfo.getColumnType();
                    if (columnInfo.getCharacterMaximumLength() != null) {
                        type = columnInfo.getColumnType() + "(" + columnInfo.getCharacterMaximumLength() + ")";
                    } else if (columnInfo.getNumericPrecision() != null) {
                        //todo
                    }
                    var column = columnInfo.getColumnName() + " " + type;

                    if (!columnInfo.getIsNullable()) {
                        column += " NOT NULL";
                    }
                    if (columnInfo.getColumnDefault() != null) {
                        column += " DEFAULT " + columnInfo.getColumnDefault();
                    }
                    return column;
                }).collect(Collectors.joining(", ", "(", ", "));
        var primaryKeyInfo = tableInformation.getPrimaryKeyInfo();
        var primaryKeyColumns = primaryKeyInfo.getColumnNames().stream()
                .collect(Collectors.joining(", ", "(", ")"));
        var sql = "CREATE TABLE " + tableName + columns + " CONSTRAINT " + primaryKeyInfo.getConstraintName() + " PRIMARY KEY " + primaryKeyColumns + ");";
        try {
            var statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            log.error("Error whe trying create table with name = {}", tableInformation.getTableName());
        }
    }

}
