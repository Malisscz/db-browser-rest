package jiri.adam.dbviewer.session;

import jiri.adam.dbviewer.db.entity.DbConnection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Data
@Slf4j
public class ConnectionHolder {
    private Connection connection;
    private DbConnection connectionInfo;

    public void closeOldConnection(){

        if (connection != null) {

            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("error closing connection, possible memory leak ... ", e);
            }
        }
    }

    @Override
    protected void finalize(){
        this.closeOldConnection();
    }
}
