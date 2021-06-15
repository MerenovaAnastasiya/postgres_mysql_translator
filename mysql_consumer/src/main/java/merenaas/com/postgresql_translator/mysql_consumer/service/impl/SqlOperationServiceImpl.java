package merenaas.com.postgresql_translator.mysql_consumer.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgresql_translator.mysql_consumer.service.ConnectionService;
import merenaas.com.postgresql_translator.mysql_consumer.service.SqlOperationService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqlOperationServiceImpl implements SqlOperationService {

    private final ConnectionService connectionService;

    @Override
    public void executeQuery(String sql) {
        var connection = connectionService.getConnection();
        try {
            var statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            log.error("Error when trying to execute query");
            log.error("QUERY = {}", sql);
        } finally {
            connectionService.closeConnection(connection);
        }
    }

}
