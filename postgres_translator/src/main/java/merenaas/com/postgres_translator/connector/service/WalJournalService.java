package merenaas.com.postgres_translator.connector.service;

import java.util.Optional;

public interface WalJournalService {
    void saveLastLsn(String slotName, String lastLsn);
    Optional<String> findLastLsn(String slotName);


}
