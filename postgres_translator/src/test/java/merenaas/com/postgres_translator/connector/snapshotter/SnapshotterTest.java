package merenaas.com.postgres_translator.connector.snapshotter;

import merenaas.com.postgres_translator.connector.service.impl.PgConnectionService;
import merenaas.com.postgres_translator.connector.service.replication.PgReplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SnapshotterTest {

    private static final String SLOT_NAME = "m11";

    @Autowired
    PgReplicationService pgReplicationService;

    @Autowired
    PgConnectionService pgConnectionService;

    @Autowired
    Snapshotter snapshotter;


    @Test
    public void test() {
        snapshotter.makeSnapshot();
//        pgReplicationService.createLogicalReplicationSlot(SLOT_NAME, "pg_mysql_decoder");
        pgReplicationService.replicateData(SLOT_NAME, "test", null);

    }


}