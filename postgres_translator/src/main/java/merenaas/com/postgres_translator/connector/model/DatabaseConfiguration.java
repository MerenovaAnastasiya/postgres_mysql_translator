package merenaas.com.postgres_translator.connector.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DatabaseConfiguration {
    private final String url;
    private final String user;
    private final String password;
}
