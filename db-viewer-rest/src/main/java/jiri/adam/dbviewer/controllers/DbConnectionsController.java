package jiri.adam.dbviewer.controllers;

import jiri.adam.dbviewer.api.JsonResponse;
import jiri.adam.dbviewer.db.DbConnectionUtils;
import jiri.adam.dbviewer.db.dao.DbConnectionService;
import jiri.adam.dbviewer.db.entity.DbConnection;
import jiri.adam.dbviewer.db.nativesql.JdbcSqlService;
import jiri.adam.dbviewer.session.ConnectionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.SQLException;
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

        log.debug("getDatabaseConnections");

        List<DbConnection> dbConnections = dbConnectionService.getDbConnections();

        return new JsonResponse("OK", dbConnections);
    }


    @RequestMapping(value = "/connect/{dbConnectionId}", method = RequestMethod.GET, consumes = "*/*", produces = "application/json")
    public JsonResponse connect(@PathVariable Integer dbConnectionId, HttpSession session) throws SQLException {

        log.debug("connect " + dbConnectionId);
        DbConnection dbConnection = dbConnectionService.getDbConnection(dbConnectionId);

        ConnectionHolder sessionConnection = (ConnectionHolder) session.getAttribute("connection");

        if (sessionConnection != null) {
            sessionConnection.closeOldConnection();
        } else {
            sessionConnection = new ConnectionHolder();
        }

        Connection connection = DbConnectionUtils.buildConnection(dbConnection);

        sessionConnection.setConnection(connection);
        sessionConnection.setConnectionInfo(dbConnection);

        session.setAttribute("connection", sessionConnection);

        log.debug("connected, fetching schema info");

        JsonResponse response = new JsonResponse("Connected to " + dbConnection.getName() + " under schema " + connection.getCatalog(), null);

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


        if(dbConnection.getId()!=null){
            return new JsonResponse("if id present, api /update should be called", null);
        }

        dbConnectionService.saveOrUpdate(dbConnection);



        DbConnection dbConnectionFromDb = dbConnectionService.getDbConnection(dbConnection.getId());

        return new JsonResponse("saved under id " + dbConnection.getId(), dbConnectionFromDb);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "*/*", produces = "application/json")
    public JsonResponse update(@RequestBody DbConnection dbConnection) {

        log.debug("update");

        if(dbConnection.getId()==null){
            return new JsonResponse("no id present", null);
        }


        dbConnectionService.saveOrUpdate(dbConnection);

        DbConnection dbConnFromDb = dbConnectionService.getDbConnection(dbConnection.getId());


        return new JsonResponse("updated conn " + dbConnection.getId(), dbConnFromDb);
    }
}
