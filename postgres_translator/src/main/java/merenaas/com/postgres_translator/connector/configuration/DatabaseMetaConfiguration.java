package merenaas.com.postgres_translator.connector.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class DatabaseMetaConfiguration {

    private static final String INFORMATION_SCHEMA = "information_schema";
    private static final String PG_CATALOG = "pg_catalog";
    private static final String PG_TOAST = "pg_toast";

    @Bean
    public Set<String> pgServiceSchemaNames() {
        return Set.of(INFORMATION_SCHEMA, PG_CATALOG, PG_TOAST);
    }
}
