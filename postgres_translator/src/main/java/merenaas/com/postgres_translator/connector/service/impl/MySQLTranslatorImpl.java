package merenaas.com.postgres_translator.connector.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgres_translator.connector.model.MySQLTableInformation;
import merenaas.com.postgres_translator.connector.service.MySQLTranslator;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MySQLTranslatorImpl implements MySQLTranslator {

    @Override
    public void createSchema(Connection connection, String schemaName) {
        var sql = "CREATE SCHEMA IF NOT EXISTS " + schemaName + ";";
        try {
            var statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            throw new RuntimeException("");
        }
    }

    @Override
    public void createTable(Connection connection, MySQLTableInformation tableInformation) {
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

    @Override
    public void executeQuery(Connection connection, String query) {
        try {
            var statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException ex) {
            System.out.println();
        }
    }
}
