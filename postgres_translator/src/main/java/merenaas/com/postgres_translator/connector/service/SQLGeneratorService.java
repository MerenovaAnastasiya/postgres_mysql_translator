package merenaas.com.postgres_translator.connector.service;

import merenaas.com.postgres_translator.connector.model.TableInformation;
import merenaas.com.postgres_translator.connector.model.TableRow;

import java.util.List;


public interface SQLGeneratorService {

    String generateCreateTableSQL(TableInformation tableInformation);
    String generateBulkInsertTableSQL(List<TableRow> tableRows);
}
