package jiri.adam.dbviewer.controllers;

import jiri.adam.dbviewer.api.JsonResponse;
import jiri.adam.dbviewer.db.DbConnectionUtils;
import jiri.adam.dbviewer.db.entity.DbConnection;
import jiri.adam.dbviewer.db.dao.DbConnectionService;
import jiri.adam.dbviewer.db.nativesql.JdbcSqlService;
import jiri.adam.dbviewer.session.ConnectionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.*;
import java.util.List;


@Slf4j
@RestController
public class DbConnectionsController {

    @Autowired
    DbConnectionService dbConnectionService;

    @Autowired
    JdbcSqlService sqlService;

    @RequestMapping(value = "/getDatabaseConnections", method = RequestMethod.GET, consumes = "*/*", produces = "application/json")
    public JsonResponse getDatabaseConnections() {

        log.debug("test");



        List<DbConnection> dbConnections = dbConnectionService.getDbConnections();

        JsonResponse response = new JsonResponse("OK", dbConnections);

        return response;
    }



    @RequestMapping(value = "/connect/{dbConnectionId}", method = RequestMethod.GET, consumes = "*/*", produces = "application/json")
    public JsonResponse connect(@PathVariable Integer dbConnectionId, HttpSession session) throws SQLException {

        log.debug("connect " + dbConnectionId);
        DbConnection dbConnection = dbConnectionService.getDbConnection(dbConnectionId);

        ConnectionHolder sessionConnection = (ConnectionHolder) session.getAttribute("connection");

        if(sessionConnection!=null){
            sessionConnection.closeOldConnection();
        }else{
            sessionConnection = new ConnectionHolder();
        }

        Connection connection = DbConnectionUtils.buildConnection(dbConnection);

        sessionConnection.setConnection(connection);
        sessionConnection.setConnectionInfo(dbConnection);

        session.setAttribute("connection" , sessionConnection);

        log.debug("connected, fetching schema info");

        JsonResponse response = new JsonResponse("Connected to " + dbConnection.getName() + " under schema "  +  connection.getCatalog(), null);

        return response;
    }

    @RequestMapping(value = "/remove/{dbConnectionId}", method = RequestMethod.GET, consumes = "*/*", produces = "application/json")
    public JsonResponse remove(@PathVariable Integer dbConnectionId) {

        log.debug("remove");
        DbConnection dbConnection = dbConnectionService.getDbConnection(dbConnectionId);
        dbConnectionService.delete(dbConnectionId);

        JsonResponse response = new JsonResponse("connecting to " + dbConnection.getName(), dbConnection);

        return response;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "*/*", produces = "application/json")
    public JsonResponse create(@RequestBody DbConnection dbConnection) {

        log.debug("create");
        dbConnectionService.saveOrUpdate(dbConnection);


        DbConnection dbConnectionFromDb = dbConnectionService.getDbConnection(dbConnection.getId());

        JsonResponse response = new JsonResponse("saved under id " + dbConnection.getId(), dbConnectionFromDb);

        return response;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "*/*", produces = "application/json")
    public JsonResponse update(@RequestBody DbConnection dbConnection) {

        log.debug("update");
        dbConnectionService.saveOrUpdate(dbConnection);

        DbConnection dbConnFromDb = dbConnectionService.getDbConnection(dbConnection.getId());;

        JsonResponse response = new JsonResponse("updated conn " + dbConnection, dbConnFromDb);

        return response;
    }
}