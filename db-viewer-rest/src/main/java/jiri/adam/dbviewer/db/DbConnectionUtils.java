package jiri.adam.dbviewer.db;

import jiri.adam.dbviewer.db.entity.DbConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class DbConnectionUtils {

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
