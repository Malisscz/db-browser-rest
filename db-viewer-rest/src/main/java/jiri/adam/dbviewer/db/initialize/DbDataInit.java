package jiri.adam.dbviewer.db.initialize;

import jiri.adam.dbviewer.db.dao.DbConnectionService;
import jiri.adam.dbviewer.db.entity.DbConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class DbDataInit {

    @Autowired
    DbConnectionService dbConnectionService;

    @PostConstruct
    void init(){

        DbConnection dbConnection = new DbConnection();
        dbConnection.setHostname("localhost");
        dbConnection.setName("Some random DB");
        dbConnection.setUsername("bcuser");
        dbConnection.setPassword("Password02");
        dbConnection.setPort("1433");
        dbConnection.setDatabaseName("testDB");
        dbConnectionService.saveOrUpdate(dbConnection);


    }

}
