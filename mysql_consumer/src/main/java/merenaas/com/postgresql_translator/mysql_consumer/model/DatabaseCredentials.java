package merenaas.com.postgresql_translator.mysql_consumer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatabaseCredentials {
    private String url;
    private String user;
    private String password;
}
