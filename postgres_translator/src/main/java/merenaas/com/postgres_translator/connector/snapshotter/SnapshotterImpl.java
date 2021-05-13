package merenaas.com.postgres_translator.connector.snapshotter;


import lombok.RequiredArgsConstructor;
import merenaas.com.postgres_translator.connector.model.SchemaInformation;
import merenaas.com.postgres_translator.connector.model.TableName;
import merenaas.com.postgres_translator.connector.service.ConnectionService;
import merenaas.com.postgres_translator.connector.service.DbTableService;
import merenaas.com.postgres_translator.connector.service.SchemaInformationService;
import merenaas.com.postgres_translator.connector.service.impl.PgConnectionService;
import merenaas.com.postgres_translator.connector.service.kafka.KafkaSenderAdapter;
import merenaas.com.postgres_translator.connector.service.replication.PgReplicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class SnapshotterImpl implements Snapshotter {

    private final SchemaInformationService schemaInformationService;
    private final PgReplicationService pgReplicationService;
    private final PgConnectionService pgConnectionService;
    private final DbTableService dbTableService;

    private final KafkaSenderAdapter kafkaSenderAdapter;
    private final ConnectionService connectionService;
    private final Set<String> replicationSchemas;

    @Value("${replication.plugin-name}")
    private String pluginName;
    @Value("${replication.slot-name}")
    private String slotName;

    public void makeSnapshot() {

        var pgConnection = connectionService.getConnection();
        //todo надо ли слот запускать???
        pgReplicationService.createLogicalReplicationSlot(slotName, pluginName);
//        pgConnectionService.setAutoCommit(false);
        setTransactionLevel(pgConnection);
        var schemaInfoByNameMap = schemaInformationService.getSchemaInfoByNames(replicationSchemas);
        replicationSchemas.forEach(schemaName -> {
            kafkaSenderAdapter.sendSyncCreateSchemaEvent(schemaInfoByNameMap.get(schemaName));
            var tableNames = schemaInformationService.getSchemaTableNames(schemaName);
            tableNames.forEach(tableName -> {
                dbTableService.shareLock(new TableName(tableName, schemaName));
                var informationAboutTable = dbTableService.getColumnsInformationAboutTable(new TableName(tableName, schemaName));
                kafkaSenderAdapter.sendAsyncCreateTableEvent(informationAboutTable);
            });
        });
    }

    private void setTransactionLevel(Connection connection) {
        try {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        } catch (SQLException ex) {
            throw new RuntimeException("Error when set transaction isolation level");
        }
    }
}
