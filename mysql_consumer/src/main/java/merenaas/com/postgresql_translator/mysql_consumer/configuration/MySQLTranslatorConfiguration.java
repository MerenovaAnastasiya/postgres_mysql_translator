package merenaas.com.postgresql_translator.mysql_consumer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class MySQLTranslatorConfiguration {

    @Bean
    public Set<String> mySqlSupportTypes() {
        return Set.of("INTEGER", "SMALLINT", "DECIMAL", "NUMERIC", "BIGINT",
                "FLOAT", "REAL", "DOUBLE PRECISION",
                "BIT", "DATE", "DATETIME", "TIMESTAMP", "TIME", "YEAR",
                "CHAR", "VARCHAR", "BINARY", "VARBINARY",
                "BLOB", "TEXT", "ENUM", "SET", "GEOMETRY", "POINT", "LINESTRING",
                "POLYGON", "GEOMETRYCOLLECTION", "MULTILINESTRING", "MULTIPOINT",
                "MULTIPOLYGON", "JSON", "BOOL");
    }
}
