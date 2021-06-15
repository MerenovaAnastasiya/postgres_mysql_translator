package merenaas.com.postgres_translator.connector.service.impl;

import lombok.RequiredArgsConstructor;
import merenaas.com.postgres_translator.connector.model.ColumnInformation;
import merenaas.com.postgres_translator.connector.model.TableInformation;
import merenaas.com.postgres_translator.connector.model.TableRow;
import merenaas.com.postgres_translator.connector.service.SQLGeneratorService;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SQLGeneratorServiceImpl implements SQLGeneratorService {

    private final Set<String> mySqlSupportTypes;

    @Override
    public String generateCreateTableSQL(TableInformation tableInformation) {
        var tableName = tableInformation.getTableName().getSchemaName() + "." + tableInformation.getTableName().getName();
        var columns = tableInformation.getColumnsInformation().stream()
                .map(columnInfo -> {
                    var type = convertType(columnInfo);
                    if (!mySqlSupportTypes.contains(type)) {
                        throw new IllegalArgumentException(String.format("Type %s is not support in MySQL", columnInfo.getColumnType()));
                    }
                    if (columnInfo.getCharacterMaximumLength() != null) {
                        type = columnInfo.getColumnType() + "(" + columnInfo.getCharacterMaximumLength() + ")";
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
        return  "CREATE TABLE " + tableName + columns + " CONSTRAINT " + primaryKeyInfo.getConstraintName() + " PRIMARY KEY " + primaryKeyColumns + ");";
    }

    @Override
    public String generateBulkInsertTableSQL(List<TableRow> tableRows) {
        if (tableRows.size() > 0) {
            var tableRow = tableRows.get(0);
            var columnNames = String.join(",", tableRow.getColumnMap().keySet());
            var sql = new StringBuilder(String.format("INSERT INTO %s.%s(%s) values ", tableRow.getTableName().getSchemaName(), tableRow.getTableName().getName(), columnNames));
            var firstColumn = true;
            for (int i = 0; i < tableRows.size(); i++) {
                if (i > 0)
                    firstColumn = false;
                if (!firstColumn)
                    sql.append(", ");
                var row = tableRows.get(i).getColumnMap().values().stream().map(value -> {
                    var strValue = value == null ? null : value.toString();
                    if (value instanceof Date || value instanceof String) {
                        strValue = "'" + strValue + "'";
                    }
                    return strValue;
                }).collect(Collectors.joining(", ", "(", ")"));
                sql.append(row);
            }
            return sql.toString();
        }
        else {
            throw new IllegalArgumentException("Empty result!");
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
