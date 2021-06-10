package merenaas.com.postgresql_translator.mysql_consumer.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgresql_translator.mysql_consumer.model.ColumnInformation;
import merenaas.com.postgresql_translator.mysql_consumer.model.SchemaInformation;
import merenaas.com.postgresql_translator.mysql_consumer.model.TableInformation;
import merenaas.com.postgresql_translator.mysql_consumer.service.ConnectionService;
import merenaas.com.postgresql_translator.mysql_consumer.service.DDLOperationService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DDLOperationServiceImpl implements DDLOperationService {

   private final ConnectionService connectionService;
    private final Set<String> mySqlSupportTypes;

    @Override
    public void createSchema(SchemaInformation schemaInformation) {
        var connection = connectionService.getConnection();
        var sql = "CREATE SCHEMA IF NOT EXISTS " + schemaInformation.getSchemaName() + ";";
        try {
            var statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            log.error("Error when trying to create schema with name {}", schemaInformation.getSchemaName());
        }
        finally {
            connectionService.closeConnection(connection);
        }
    }

    @Override
    public void createTable(TableInformation tableInformation) {
        var tableName = tableInformation.getTableName().getSchemaName() + "." + tableInformation.getTableName().getName();
        var columns = tableInformation.getColumnsInformation().stream()
                .map(columnInfo -> {
                    var type = convertType(columnInfo);
                    if (!mySqlSupportTypes.contains(type)) {
                        throw new IllegalArgumentException(String.format("Type %s is not support in MySQL", columnInfo.getColumnType()));
                    }
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
        var connection = connectionService.getConnection();
        try {
            var statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            log.error("Error whe trying create table with name = {}", tableInformation.getTableName());
        }
        finally {
            connectionService.closeConnection(connection);
        }
    }

    private String convertType(ColumnInformation information) {
        var type = information.getColumnType().toUpperCase();
        var mySQLType = type;
        switch (type) {
            case "CHARACTER VARYING":
                if (information.getCharacterMaximumLength() == null) {
                    mySQLType = "TEXT";
                }
                else {
                    mySQLType = "VARCHAR";
                }
                break;
            case "CHARACTER":
                mySQLType = "CHAR";
                break;
            case "TIMESTAMP WITHOUT TIME ZONE":
                mySQLType = "TIMESTAMP";
                break;
            case "TIME WITHOUT TIME ZONE":
                mySQLType = "TIME";
                break;
            case "BOOLEAN":
                mySQLType = "BOOL";
                break;
        }
        return mySQLType;
    }

}
