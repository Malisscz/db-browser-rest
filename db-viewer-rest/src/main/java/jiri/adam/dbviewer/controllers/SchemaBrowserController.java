package jiri.adam.dbviewer.controllers;

import jiri.adam.dbviewer.api.JsonResponse;
import jiri.adam.dbviewer.db.dao.DbConnectionService;
import jiri.adam.dbviewer.db.nativesql.JdbcSqlService;
import jiri.adam.dbviewer.db.nativesql.model.SqlQueryResult;
import jiri.adam.dbviewer.session.ConnectionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.SQLException;


@Slf4j
@RestController
@RequestMapping(value = "/browse")
public class SchemaBrowserController {

    @Autowired
    DbConnectionService dbConnectionService;

    @Autowired
    JdbcSqlService sqlService;

    @RequestMapping(value = "/schemas", method = RequestMethod.GET, consumes = "*/*", produces = "application/json")
    public JsonResponse schemas(HttpSession session) throws SQLException {

        log.debug("browseConnection");

        ConnectionHolder connection = (ConnectionHolder) session.getAttribute("connection");

        if(connection==null){
            return new JsonResponse("login-required", "you must call /connect/{connectionId} first");
        }

        SqlQueryResult resultRow = sqlService.getDatabaseSchemas(connection.getConnection());

        JsonResponse response = new JsonResponse("OK", resultRow);

        return response;
    }


    @RequestMapping(value = "/schema/{schemaName}", method = RequestMethod.GET, consumes = "*/*", produces = "application/json")
    public JsonResponse browseConnection(HttpSession session, @PathVariable String schemaName) throws SQLException {

        log.debug("browseConnection");

        ConnectionHolder connection = (ConnectionHolder) session.getAttribute("connection");

        if(connection==null){
            return new JsonResponse("login-required", "you must call /connect/{connectionId} first or call /browse/schema/{connectionId}");
        }

        SqlQueryResult resultRow = sqlService.getSchemaTables(connection.getConnection(), schemaName);

        JsonResponse response = new JsonResponse("OK", resultRow);

        return response;
    }


    @RequestMapping(value = "/columns/{schema}/{tableName}", method = RequestMethod.GET, consumes = "*/*", produces = "application/json")
    public JsonResponse tableColumns(HttpSession session, @PathVariable String tableName, @PathVariable String schema) throws SQLException {

        ConnectionHolder connection = (ConnectionHolder) session.getAttribute("connection");

        if(connection==null){
            return new JsonResponse("login-required", "you must call /connect/{id} first");
        }

        SqlQueryResult resultRow = sqlService.getTableColumns(connection.getConnection(),schema, tableName);

        JsonResponse response = new JsonResponse("OK", resultRow);

        return response;
    }

    @RequestMapping(value = "/data/{schema}/{tableName}", method = RequestMethod.GET, consumes = "*/*", produces = "application/json")
    public JsonResponse tableData(HttpSession session, @PathVariable String tableName, @PathVariable String schema) throws SQLException {

        ConnectionHolder connection = (ConnectionHolder) session.getAttribute("connection");

        if(connection==null){
            return new JsonResponse("login-required", "you must call /connect/{id} first");
        }

        SqlQueryResult results = sqlService.getTableRawData(connection.getConnection(), schema, tableName);

        JsonResponse response = new JsonResponse("OK", results);
        return response;
    }

}
