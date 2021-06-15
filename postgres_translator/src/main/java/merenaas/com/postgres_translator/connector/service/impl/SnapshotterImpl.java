package merenaas.com.postgres_translator.connector.service.impl;


import lombok.RequiredArgsConstructor;
import merenaas.com.postgres_translator.connector.model.TableName;
import merenaas.com.postgres_translator.connector.service.ConnectionService;
import merenaas.com.postgres_translator.connector.service.SQLGeneratorService;
import merenaas.com.postgres_translator.connector.service.TableService;
import merenaas.com.postgres_translator.connector.service.SchemaInformationService;
import merenaas.com.postgres_translator.connector.service.Snapshotter;
import merenaas.com.postgres_translator.connector.service.kafka.KafkaSenderAdapter;
import merenaas.com.postgres_translator.connector.service.replication.PgReplicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class SnapshotterImpl implements Snapshotter {

    private final SchemaInformationService schemaInformationService;
    private final PgReplicationService pgReplicationService;
    private final PgConnectionService pgConnectionService;
    private final TableService tableService;
    private final SQLGeneratorService sqlGeneratorService;
    private final KafkaSenderAdapter kafkaSenderAdapter;
    private final ConnectionService connectionService;
    private final Set<String> replicationSchemas;

    @Value("${replication.plugin-name}")
    private String pluginName;
    @Value("${replication.slot-name}")
    private String slotName;
    @Value("${replication.select-limit:500}")
    private Integer limit;

    public void makeSnapshot() {
        var pgConnection = connectionService.getConnection();
//        pgReplicationService.createLogicalReplicationSlot(slotName, pluginName);
        pgConnectionService.setAutoCommit(false);
//        setTransactionLevel(pgConnection);
        replicationSchemas.forEach(schemaName -> {
            var tableNames = schemaInformationService.getSchemaTableNames(schemaName);
            tableNames.forEach(tableName -> {
                var fullTableName = new TableName(tableName, schemaName);
                //2. блокируем таблицы на запись до полной отправки данных в кафка
                tableService.shareLock(fullTableName);
                var informationAboutTable = tableService.getColumnsInformationAboutTable(new TableName(tableName, schemaName));
                //3. генерируем таблицы
                kafkaSenderAdapter.sendSyncCreateTableEvent(informationAboutTable);
                //4. генерируем данные таблицы
                var offset = 0;
                while (true) {
                    var selectResult = tableService.selectFromTable(fullTableName, limit, offset);
                    var insertQuery = sqlGeneratorService.generateBulkInsertTableSQL(selectResult.getResult());
                    kafkaSenderAdapter.sendAsyncDMlEvent(fullTableName, insertQuery);
                    offset += limit;
                    if (!selectResult.hasNext()) {
                        break;
                    }
                }
                
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
