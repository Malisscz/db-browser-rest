package jiri.adam.dbviewer.session;

import jiri.adam.dbviewer.db.entity.DbConnection;
import jiri.adam.dbviewer.db.nativesql.JdbcSqlService;
import jiri.adam.dbviewer.db.nativesql.model.SqlQueryResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Wrapper for SQL injection and DbConnection entity
 */
@Data
@Slf4j
public class ConnectionHolder {
    private Connection connection;
    private DbConnection connectionInfo;

    public void closeOldConnection() {

        if (connection != null) {

            try {
                log.trace("closing connection ...");
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("error closing connection, possible memory leak ... ", e);
            }
        }
    }

    @Override
    protected void finalize() {
        this.closeOldConnection();
    }

    /**
     * Checks if provided schema is located within connection
     * @param sqlService service object
     * @param schema to be validated within connection
     * @throws SQLException if schema doesnt exist, otherwise does nothing
     */
    public void isValidSchema(JdbcSqlService sqlService, String schema) throws SQLException {

        SqlQueryResult databaseSchemas = sqlService.getDatabaseSchemas(connection);

        for(List<Object> schemaDetails : databaseSchemas.getRows()){
            if(((String)schemaDetails.get(0)).equalsIgnoreCase(schema)){
                return;
            }
        }

        throw new SQLException("schema doesnt exist");

    }


    /**
     * Checks if provided schema is located within connection
     * @param sqlService service object
     * @param schema to be validated within connection
     * @param tableName table to be validated within schema
     * @throws SQLException if schema doesnt exist, otherwise does nothing
     */
    public void isValidTableWithinSchema(JdbcSqlService sqlService, String schema, String tableName) throws SQLException {

        //schema is valid at this point
        SqlQueryResult databaseSchemas = sqlService.getSchemaTables(connection, schema);

        for(List<Object> schemaDetails : databaseSchemas.getRows()){
            if(((String)schemaDetails.get(1)).equalsIgnoreCase(tableName)){
                return;
            }
        }

        throw new SQLException("schema / table doesnt exist");

    }
}
