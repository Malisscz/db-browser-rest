package jiri.adam.dbviewer.db.initialize;

import jiri.adam.dbviewer.db.dao.DbConnectionService;
import jiri.adam.dbviewer.db.entity.DbConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class DbDataInit {

    @Autowired
    DbConnectionService dbConnectionService;

    @PostConstruct
    void init() {

        DbConnection dbConnection = new DbConnection();
        dbConnection.setHostname("ia-sql01.trask.cz");
        dbConnection.setName("Test MS SQL DB");
        dbConnection.setUsername("bcuser");
        dbConnection.setPassword("Password02");
        dbConnection.setPort("1433");
        dbConnection.setDatabaseName("USIGNDB_AKT");
        dbConnectionService.saveOrUpdate(dbConnection);

        DbConnection dbConnection2 = new DbConnection();
        dbConnection2.setHostname("localhost");
        dbConnection2.setName("Some random DB");
        dbConnection2.setUsername("bcuser");
        dbConnection2.setPassword("Password02");
        dbConnection2.setPort("1433");
        dbConnection2.setDatabaseName("testDB");
        dbConnectionService.saveOrUpdate(dbConnection2);


        DbConnection dbConnection3 = new DbConnection();
        dbConnection3.setHostname("localhost");
        dbConnection3.setName("Some other random DB");
        dbConnection3.setUsername("aauser");
        dbConnection3.setPassword("Password01");
        dbConnection3.setPort("1433");
        dbConnection3.setDatabaseName("aaDB");
        dbConnectionService.saveOrUpdate(dbConnection3);


    }

}
