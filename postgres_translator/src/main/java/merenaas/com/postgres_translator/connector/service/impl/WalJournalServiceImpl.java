package merenaas.com.postgres_translator.connector.service.impl;

import lombok.RequiredArgsConstructor;
import merenaas.com.postgres_translator.connector.service.ConnectionService;
import merenaas.com.postgres_translator.connector.service.WalJournalService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class WalJournalServiceImpl implements WalJournalService {

    private final ConnectionService connectionService;

    @Override
    public long readCurrentPosition() {
        var connection = connectionService.getConnection();
        try {
            var sql = "SELECT txid_current FROM txid_current()";
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getLong("txid_current");
        } catch (SQLException exception) {
            throw new RuntimeException("Exception when trying get current wal offset");
        }
    }
}
