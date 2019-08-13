package jiri.adam.dbviewer.controllers;

import jiri.adam.dbviewer.model.JsonResponse;
import jiri.adam.dbviewer.db.dao.DbConnectionService;
import jiri.adam.dbviewer.db.nativesql.JdbcSqlService;
import jiri.adam.dbviewer.db.nativesql.model.AggregationStatistics;
import jiri.adam.dbviewer.db.nativesql.model.ColumnStats;
import jiri.adam.dbviewer.db.nativesql.model.SqlQueryResult;
import jiri.adam.dbviewer.db.nativesql.model.TableAggregationStatistics;
import jiri.adam.dbviewer.session.ConnectionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Endpoint implementing bonus task
 */
@Slf4j
@RestController
@RequestMapping(value = "/analytics")
public class ColumnStatisticsController {

    @Autowired
    DbConnectionService dbConnectionService;

    @Autowired
    JdbcSqlService sqlService;

    @RequestMapping(value = "/stats/{schema}", method = RequestMethod.GET, consumes = "*/*", produces = "application/json")
    public JsonResponse tableStats(HttpSession session, @PathVariable String schema) throws SQLException {

        ConnectionHolder connection = (ConnectionHolder) session.getAttribute("connection");

        if (connection == null) {
            return new JsonResponse("login-required", "you must call /connect/{id} first");
        }

        log.trace("validating sql injection based on schema metadata ...");
        connection.isValidSchema(sqlService, schema);
        log.trace("valid");

        SqlQueryResult tables = sqlService.getSchemaTables(connection.getConnection(), schema);

        AggregationStatistics agg = new AggregationStatistics();

        List<TableAggregationStatistics> tableStats = new LinkedList<>();

        for (List<Object> tableInfo : tables.getRows()) {
            String tableName = (String) tableInfo.get(1);

            TableAggregationStatistics tableAggr = new TableAggregationStatistics();
            tableAggr.setTableName(tableName);
            List<ColumnStats> tableColumnStats = new LinkedList<>();
            SqlQueryResult tableColumns = sqlService.getTableColumns(connection.getConnection(), schema, tableName);
            tableAggr.setColumnsCount(tableColumns.getColumnNames().size());
            tableAggr.setRowsCount(sqlService.countTableRows(connection.getConnection(), schema, tableName));
            for (List<Object> columnInfo : tableColumns.getRows()) {

                String columnName = (String) columnInfo.get(2);
                String columnType = (String) columnInfo.get(3);

                if ("bigint".equals(columnType) || "int".equals(columnType)) {
                    ColumnStats columnStats = sqlService.getTableColumnStats(connection.getConnection(), schema, tableName, columnName);
                    columnStats.setColumnName(columnName);
                    tableColumnStats.add(columnStats);
                }

            }
            tableAggr.setColumnStats(tableColumnStats);
            tableStats.add(tableAggr);

        }

        agg.setAggregationStatistics(tableStats);

        return new JsonResponse("OK", agg);
    }


}
