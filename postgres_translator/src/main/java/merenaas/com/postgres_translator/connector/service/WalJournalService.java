package merenaas.com.postgres_translator.connector.service;

import java.sql.Connection;

public interface WalJournalService {

    long readCurrentPosition(Connection connection);
}
