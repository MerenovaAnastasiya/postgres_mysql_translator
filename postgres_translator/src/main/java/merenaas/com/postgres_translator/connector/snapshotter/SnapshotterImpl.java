package merenaas.com.postgres_translator.connector.snapshotter;


import lombok.RequiredArgsConstructor;
import merenaas.com.postgres_translator.connector.model.DatabaseConfiguration;
import merenaas.com.postgres_translator.connector.model.MySQLTableInformation;
import merenaas.com.postgres_translator.connector.model.PgTableInformation;
import merenaas.com.postgres_translator.connector.model.TableName;
import merenaas.com.postgres_translator.connector.service.DbMetaService;
import merenaas.com.postgres_translator.connector.service.DbTableService;
import merenaas.com.postgres_translator.connector.service.MySQLTranslator;
import merenaas.com.postgres_translator.connector.service.PgReplicationService;
import merenaas.com.postgres_translator.connector.service.SchemaInformationService;
import merenaas.com.postgres_translator.connector.service.impl.MySqlConnectionService;
import merenaas.com.postgres_translator.connector.service.impl.PgConnectionService;
import merenaas.com.postgres_translator.connector.util.PgMySQLTypesConvertor;
import org.postgresql.jdbc.PgConnection;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class SnapshotterImpl implements Snapshotter {

    private final SchemaInformationService schemaInformationService;
    private final DbTableService dbTableService;
    private final PgConnectionService pgConnectionService;
    private final MySqlConnectionService mySqlConnectionService;
    private final DbMetaService dbMetaService;
    private final MySQLTranslator mySQLTranslator;
    private final PgMySQLTypesConvertor pgMySQLTypesConvertor;
    private final PgReplicationService pgReplicationService;

    public void makeSnapshot(DatabaseConfiguration pgConfiguration, DatabaseConfiguration mySqlConfiguration, String replicationSlotName) {
        var pgConnection = pgConnectionService.createConnection(pgConfiguration);
        //todo надо ли слот запускать???
        var replicationConnection = pgConnectionService.unwrap(pgConnection, PgConnection.class);
        pgReplicationService.createLogicalReplicationSlot(replicationConnection, replicationSlotName);
        pgConnectionService.setAutoCommit(pgConnection, false);
        setTransactionLevel(pgConnection);
        Set<String> schemaNames = dbMetaService.getDatabaseSchemaNames(pgConnection);
        var mySqlConnection = mySqlConnectionService.createConnection(mySqlConfiguration);
        schemaNames.forEach(schemaName -> {
            mySQLTranslator.createSchema(mySqlConnection, schemaName);
            Collection<String> tableNames = schemaInformationService.getSchemaTableNames(pgConnection, schemaName);
            tableNames.forEach(tableName -> {
                //todo обрамить в транзакцию
                dbTableService.shareLock(pgConnection, new TableName(tableName, schemaName));
                PgTableInformation informationAboutTable = dbTableService.getColumnsInformationAboutTable(pgConnection, new TableName(tableName, schemaName));
                MySQLTableInformation mySQLTableColumnsInformation = pgMySQLTypesConvertor.convert(informationAboutTable);
                mySQLTranslator.createTable(mySqlConnection, mySQLTableColumnsInformation);
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
