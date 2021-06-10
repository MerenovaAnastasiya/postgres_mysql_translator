package merenaas.com.postgresql_translator.mysql_consumer.service;

import merenaas.com.postgresql_translator.mysql_consumer.model.SchemaInformation;
import merenaas.com.postgresql_translator.mysql_consumer.model.TableInformation;

public interface DDLOperationService {

    void createSchema(SchemaInformation schemaInformation);
    void createTable(TableInformation tableInformation);

}
