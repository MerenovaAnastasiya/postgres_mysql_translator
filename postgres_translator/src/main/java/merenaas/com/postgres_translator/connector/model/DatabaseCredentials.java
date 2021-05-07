package merenaas.com.postgres_translator.connector.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DatabaseCredentials {
    private String url;
    private String user;
    private String password;
}
