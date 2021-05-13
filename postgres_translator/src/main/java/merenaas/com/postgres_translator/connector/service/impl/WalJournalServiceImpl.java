package merenaas.com.postgres_translator.connector.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merenaas.com.postgres_translator.connector.service.ConnectionService;
import merenaas.com.postgres_translator.connector.service.WalJournalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalJournalServiceImpl implements WalJournalService {

    private final ConnectionService connectionService;
    @Value("${replication.lsn-table-name}")
    private String lsnTableName;

    @Override
    public void saveLastLsn(String slotName, String lastLsn){
        var connection = connectionService.getConnection();
        var sql = "INSERT INTO " + lsnTableName + "(lsn, slot_name, time) VALUES(?, ?, now())";
        try {
            var statement = connection.prepareStatement(sql);
            statement.setString(1, lastLsn);
            statement.setString(2, slotName);
            statement.execute();
        } catch (SQLException e) {
            log.error(e.getMessage());
        } finally {
            connectionService.closeConnection(connection);
        }
    }

    @Override
    public Optional<String> findLastLsn(String slotName) {
        var connection = connectionService.getConnection();
        var sql = "SELECT lsn FROM " + lsnTableName + " WHERE slot_name = ? ORDER BY time DESC LIMIT 1";
        try {
            var statement = connection.prepareStatement(sql);
            statement.setString(1, slotName);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.ofNullable(resultSet.getString("lsn"));
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.warn("SQL exception when trying to get last lsn");
            return Optional.empty();
        } finally {
           connectionService.closeConnection(connection);
        }
    }
}
