package merenaas.com.postgresql_translator.mysql_consumer.service.impl;

import lombok.RequiredArgsConstructor;
import merenaas.com.postgresql_translator.mysql_consumer.connection.ConnectionHolder;
import merenaas.com.postgresql_translator.mysql_consumer.service.DMLOperationService;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class DMLOperationServiceImpl implements DMLOperationService {

    private final ConnectionHolder connectionHolder;

    @Override
    public void executeQuery(String query) {
        var connection = connectionHolder.getConnection();
        try {
            var statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException ex) {
            throw new RuntimeException("Error while executing the SQL query");
        }
    }

}
