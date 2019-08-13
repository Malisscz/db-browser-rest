package jiri.adam.dbviewer.db;

import jiri.adam.dbviewer.db.entity.DbConnection;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class DbConnectionUtils {

    /**
     * Builds JDBC connection to Microsoft SQL Server database
     * @param dbConnection entity to read connection info from
     * @return the JDBC Connection
     * @throws SQLException
     */
    public static Connection buildConnection(DbConnection dbConnection) throws SQLException {

        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", dbConnection.getUsername());
        connectionProps.put("password", dbConnection.getPassword());


        conn = DriverManager.getConnection(
                "jdbc:sqlserver://" +
                        dbConnection.getHostname() +
                        ":" + dbConnection.getPort() +
                        ";databaseName=" + dbConnection.getDatabaseName(),
                connectionProps);


        return conn;
    }


}
