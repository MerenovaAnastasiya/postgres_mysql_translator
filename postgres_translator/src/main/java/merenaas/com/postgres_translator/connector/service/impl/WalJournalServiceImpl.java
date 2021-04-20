package merenaas.com.postgres_translator.connector.service.impl;

import merenaas.com.postgres_translator.connector.service.WalJournalService;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class WalJournalServiceImpl implements WalJournalService {

    //TODO сохранять lsn при падении

    @Override
    public long readCurrentPosition(Connection connection) {
        try {
            String sql = "SELECT txid_current FROM txid_current()";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet.getLong("txid_current");
        } catch (SQLException exception) {
            throw new RuntimeException("Exception when trying get current wal offset");
        }
    }
}
