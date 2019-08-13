package jiri.adam.dbviewer.db.nativesql;

import jiri.adam.dbviewer.db.nativesql.model.ColumnStats;
import jiri.adam.dbviewer.db.nativesql.model.SqlQueryResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for browsing selected database connection, searching schema and table metadata as well as searching table data
 */
@Service
@Slf4j
public class JdbcSqlService {


    /**
     * Retrieves table's columns for a specific schema, the sql query is executed via @{@link PreparedStatement} object
     *
     * @param connection JDBC active connection
     * @param schema     database schema
     * @param tableName  schema's table
     * @return @{@link SqlQueryResult} object filled with column metadata
     * @throws SQLException
     */
    public SqlQueryResult getTableColumns(Connection connection, String schema, String tableName) throws SQLException {


        String columnDetailsSql = "SELECT S.name as schema_name, " +
                "    T.name AS Table_Name , " +
                "    C.name AS Column_Name , " +
                "    P.name AS Data_Type , " +
                "    CAST(C.is_identity as BIT) as PRIMARY_KEY, " +
                "    P.max_length AS Size , " +
                "    CAST(P.precision AS VARCHAR) + '/' + CAST(P.scale AS VARCHAR) AS Precision_Scale " +
                "    FROM   sys.objects AS T " +
                "    JOIN sys.columns AS C ON T.object_id = C.object_id " +
                "    JOIN sys.types AS P ON C.system_type_id = P.system_type_id " +
                "    JOIN sys.schemas as S on S.schema_id = T.schema_id " +
                "    WHERE  T.type_desc = 'USER_TABLE' and S.name = ? and T.name = ?";


        PreparedStatement preparedStatement = connection.prepareStatement(columnDetailsSql);
        preparedStatement.setString(1, schema);
        preparedStatement.setString(2, tableName);

        ResultSet rs = preparedStatement.executeQuery();
        return processResultSet(rs);
    }


    /**
     * Retrieves table's data rows for a specific, the sql query is executed via @{@link PreparedStatement} object, vulnerable to SQL injection,
     * should be called after @{@link jiri.adam.dbviewer.session.ConnectionHolder#isValidSchema(JdbcSqlService, String)}
     * and@{@link jiri.adam.dbviewer.session.ConnectionHolder#isValidTableWithinSchema(JdbcSqlService, String, String)}
     * verifies schema and table exists, no paging implemented
     *
     * @param connection JDBC active connection
     * @param schema     database schema
     * @param tableName  schema's table
     * @return @{@link SqlQueryResult} object filled with raw data
     * @throws SQLException
     */
    public SqlQueryResult getTableRawData(Connection connection, String schema, String tableName) throws SQLException {

        String sql = "select * from  " + schema + "." + tableName;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet rs = preparedStatement.executeQuery();
        return processResultSet(rs);


    }

    /**
     * Retrieves tables for a specific schema, the sql query is executed via @{@link PreparedStatement} object
     *
     * @param connection JDBC active connection
     * @param schemaName database schema
     * @return @{@link SqlQueryResult} object filled with schema's metadata about tables
     * @throws SQLException
     */
    public SqlQueryResult getSchemaTables(Connection connection, String schemaName) throws SQLException {

        String sql = "SELECT      u.name AS [schema],   " +
                " t.name AS [table],  " +
                "            td.value AS [table_desc],  " +
                "            c.name AS [column],  " +
                "            cd.value AS [column_desc]  " +
                "FROM        sysobjects t  " +
                "INNER JOIN  sysusers u  " +
                "    ON      u.uid = t.uid  " +
                "LEFT OUTER JOIN sys.extended_properties td  " +
                "    ON      td.major_id = t.id  " +
                "    AND     td.minor_id = 0  " +
                "    AND     td.name = 'MS_Description'  " +
                "INNER JOIN  syscolumns c  " +
                "    ON      c.id = t.id  " +
                "LEFT OUTER JOIN sys.extended_properties cd  " +
                "    ON      cd.major_id = c.id  " +
                "    AND     cd.minor_id = c.colid  " +
                "    AND     cd.name = 'MS_Description'  " +
                " where u.name = ? " +
                "ORDER BY    t.name, c.colorder";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, schemaName);

        ResultSet rs = preparedStatement.executeQuery();
        return processResultSet(rs);

    }


    /**
     * Retrieves schemas for selected jdbc connection
     *
     * @param connection JDBC active connection
     * @return @{@link SqlQueryResult} object filled with database's metadata
     * @throws SQLException
     */
    public SqlQueryResult getDatabaseSchemas(Connection connection) throws SQLException {


        String sql = "select s.name as schema_name, " +
                "                s.schema_id, " +
                "                u.name as schema_owner " +
                "        from sys.schemas s " +
                "        inner join sys.sysusers u " +
                "        on u.uid = s.principal_id " +
                "        order by s.name";


        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet rs = preparedStatement.executeQuery();
        return processResultSet(rs);


    }

    /**
     * Retrieves column statistics, min, max, avg, median for a column,
     * the sql query is executed via @{@link PreparedStatement} object, vulnerable to SQL injection,
     * should be called after @{@link jiri.adam.dbviewer.session.ConnectionHolder#isValidSchema(JdbcSqlService, String)}
     * and@{@link jiri.adam.dbviewer.session.ConnectionHolder#isValidTableWithinSchema(JdbcSqlService, String, String)}
     * verifies schema and table exists
     * or
     * to be called with no user input
     *
     * @param connection JDBC active connection
     * @param schema     database schema
     * @param tableName  schema's table
     * @param columnName table's column name, column has to be of numerical type
     * @return @{@link ColumnStats} object filled with raw data
     * @throws SQLException
     */
    public ColumnStats getTableColumnStats(Connection connection, String schema, String tableName, String columnName) throws SQLException {

        String minMaxAvg = "select min(" + columnName + ") as min, max(" + columnName + ") as max, avg(" + columnName + ") as avg from " + schema + "." + tableName;

        String median = "SELECT ( " +
                " (SELECT MAX(" + columnName + ") FROM " +
                "   (SELECT TOP 50 PERCENT " + columnName + " FROM " + schema + "." + tableName + " ORDER BY " + columnName + ") AS BottomHalf) " +
                " + " +
                " (SELECT MIN(" + columnName + ") FROM " +
                "   (SELECT TOP 50 PERCENT " + columnName + " FROM " + schema + "." + tableName + " ORDER BY " + columnName + " DESC) AS TopHalf) " +
                ") / 2 AS Median";

        PreparedStatement preparedStatement = connection.prepareStatement(minMaxAvg);

        ResultSet rs = preparedStatement.executeQuery();
        SqlQueryResult sqlQueryResult = processResultSet(rs);


        PreparedStatement preparedStatementMed = connection.prepareStatement(median);
        ResultSet rsMed = preparedStatementMed.executeQuery();
        SqlQueryResult sqlQueryResultMed = processResultSet(rsMed);

        ColumnStats statisticsResult = new ColumnStats();

        Object min = sqlQueryResult.getRows().get(0).get(0);
        if (min instanceof Long) {
            statisticsResult.setMin(((Long) min));
        } else if (min instanceof Integer) {
            statisticsResult.setMin(((Integer) min).longValue());
        }

        Object max = sqlQueryResult.getRows().get(0).get(1);
        if (max instanceof Long) {
            statisticsResult.setMax(((Long) max));
        } else if (max instanceof Integer) {
            statisticsResult.setMax(((Integer) max).longValue());
        }

        Object avg = sqlQueryResult.getRows().get(0).get(2);
        if (avg instanceof Long) {
            statisticsResult.setAvg(((Long) avg).doubleValue());
        } else if (avg instanceof Double) {
            statisticsResult.setAvg((Double) avg);
        }


        Object med = sqlQueryResultMed.getRows().get(0).get(0);
        if (med instanceof Long) {
            statisticsResult.setMedian(((Long) med));
        } else if (med instanceof Integer) {
            statisticsResult.setMedian(((Integer) med).longValue());
        }

        return statisticsResult;
    }

    /**
     * Retrieves count of rows
     * the sql query is executed via @{@link PreparedStatement} object, vulnerable to SQL injection,
     * should be called after @{@link jiri.adam.dbviewer.session.ConnectionHolder#isValidSchema(JdbcSqlService, String)}
     * and@{@link jiri.adam.dbviewer.session.ConnectionHolder#isValidTableWithinSchema(JdbcSqlService, String, String)}
     * verifies schema and table exists
     * or
     * to be called with no user input
     *
     * @param connection JDBC active connection
     * @param schema     database schema
     * @param tableName  schema's table
     * @return row count
     * @throws SQLException
     */
    public Long countTableRows(Connection connection, String schema, String tableName) throws SQLException {
        String countSql = "select count(*) as rows_count from " + schema + "." + tableName;

        PreparedStatement preparedStatement = connection.prepareStatement(countSql);

        ResultSet rs = preparedStatement.executeQuery();
        SqlQueryResult sqlQueryResult = processResultSet(rs);


        Object count = sqlQueryResult.getRows().get(0).get(0);
        if (count instanceof Long) {
            return (Long) count;
        } else if (count instanceof Integer) {
            return ((Integer) count).longValue();
        }

        return -1L;
    }


    private SqlQueryResult processResultSet(ResultSet rs) throws SQLException {

        SqlQueryResult resultRow = new SqlQueryResult();

        ResultSetMetaData rsmd = rs.getMetaData();

        int totalColumnNumber = rsmd.getColumnCount();

        List<String> resultSetColumnNames = getResultSetColumnNames(rs, totalColumnNumber);
        resultRow.setColumnNames(resultSetColumnNames);

        while (rs.next()) {

            List<Object> columns = getColumnCellValueAsObjects(rs, totalColumnNumber);
            resultRow.getRows().add(columns);
        }

        return resultRow;

    }

    private List<String> getResultSetColumnNames(ResultSet rs, int totalColumnNumber) throws SQLException {
        List<String> names = new ArrayList<>(totalColumnNumber);

        for (int i = 1; i <= totalColumnNumber; i++) {
            String column = rs.getMetaData().getColumnName(i);
            names.add(column);
            //log.trace("adding column: " + column);
        }
        return names;
    }

    private List<Object> getColumnCellValueAsObjects(ResultSet rs, int totalColumnNumber) throws SQLException {

        List<Object> objects = new ArrayList<>(totalColumnNumber);

        for (int i = 1; i <= totalColumnNumber; i++) {
            Object column = rs.getObject(i);
            objects.add(column);
        }

        return objects;
    }

}
