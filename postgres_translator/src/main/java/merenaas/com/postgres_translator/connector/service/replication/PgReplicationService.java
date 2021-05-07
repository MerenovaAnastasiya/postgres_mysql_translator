package merenaas.com.postgres_translator.connector.service.replication;

import org.springframework.lang.Nullable;

import java.util.Properties;

public interface PgReplicationService {

    void createLogicalReplicationSlot(String slotName, String pluginName);
    void replicateData(String slotName, String schemaName, @Nullable Properties slotOptions);
}
