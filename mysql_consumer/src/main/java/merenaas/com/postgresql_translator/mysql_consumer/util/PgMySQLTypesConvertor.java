//package merenaas.com.postgresql_translator.mysql_consumer.util;
//
//import lombok.RequiredArgsConstructor;
//import merenaas.com.postgresql_translator.mysql_consumer.model.ColumnInformation;
//import org.springframework.stereotype.Component;
//
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//public class PgMySQLTypesConvertor {
//
//    private final Set<String> mySqlSupportTypes;
//
//    public MySQLTableInformation convert(PgTableInformation pgColumnsInformation) {
//        var columnsInformation = pgColumnsInformation.getColumnsInformation()
//                .stream().map(info -> {
//                    var name = info.getColumnName();
//                    var type = convertType(info);
//                    if(!mySqlSupportTypes.contains(type)) {
//                        throw new IllegalArgumentException(String.format("Type %s is not support in MySQL", info.getColumnType()));
//                    }
//                    var isNullable = info.getIsNullable();
//                    var characterMaximumLength = info.getCharacterMaximumLength();
//                    var columnDefault = info.getColumnDefault();
//                    var numericPrecision = info.getNumericPrecision();
//                    return MySQLColumnInformation.builder()
//                            .columnName(name)
//                            .columnDefault(columnDefault)
//                            .columnType(type)
//                            .isNullable(isNullable)
//                            .characterMaximumLength(characterMaximumLength)
//                            .numericPrecision(numericPrecision)
//                            .build();
//                }).collect(Collectors.toSet());
//        return new MySQLTableInformation(pgColumnsInformation.getTableName(), columnsInformation, pgColumnsInformation.getPrimaryKeyInfo());
//
//    }
//
////    private String convertType(ColumnInformation information) {
////        var type = information.getColumnType().toUpperCase();
////        var mySQLType = type;
////        switch (type) {
////            case "CHARACTER VARYING":
////                if (information.getCharacterMaximumLength() == null) {
////                    mySQLType = "TEXT";
////                }
////                else {
////                    mySQLType = "VARCHAR";
////                }
////                break;
////            case "CHARACTER":
////                mySQLType = "CHAR";
////                break;
////            case "TIMESTAMP WITHOUT TIME ZONE":
////                mySQLType = "TIMESTAMP";
////                break;
////            case "TIME WITHOUT TIME ZONE":
////                mySQLType = "TIME";
////                break;
////            case "BOOLEAN":
////                mySQLType = "BOOL";
////                break;
////        }
////        return mySQLType;
////    }
//}
