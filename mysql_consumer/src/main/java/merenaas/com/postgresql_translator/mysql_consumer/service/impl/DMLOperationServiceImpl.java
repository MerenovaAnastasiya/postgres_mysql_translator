package merenaas.com.postgresql_translator.mysql_consumer.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgresql_translator.mysql_consumer.service.ConnectionService;
import merenaas.com.postgresql_translator.mysql_consumer.service.DMLOperationService;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
@Slf4j
@RequiredArgsConstructor
public class DMLOperationServiceImpl implements DMLOperationService {

    private final ConnectionService connectionService;

    @Override
    public void executeQuery(String query) {
        var connection = connectionService.getConnection();
        try {
            var statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException ex) {
            log.error("Error when trying execute query: {}", query);
        }
        finally {
            connectionService.closeConnection(connection);
        }
    }

}
