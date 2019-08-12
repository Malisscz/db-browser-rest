package jiri.adam.dbviewer.db.nativesql;

import jiri.adam.dbviewer.db.nativesql.model.SqlQueryResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class JdbcSqlService {


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

        for(int i = 1 ; i <= totalColumnNumber; i++){
            String column = rs.getMetaData().getColumnName(i);
            names.add(column);
            //log.trace("adding column: " + column);
        }
        return names;
    }

    private List<Object> getColumnCellValueAsObjects(ResultSet rs, int totalColumnNumber) throws SQLException {

        List<Object> objects = new ArrayList<>(totalColumnNumber);

        for(int i = 1 ; i <= totalColumnNumber; i++){
            Object column = rs.getObject(i);
            objects.add(column);
        }

        return objects;
    }

    ;
    
    
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
                "    WHERE  T.type_desc = 'USER_TABLE' and S.name = '"+schema+"' and T.name = '"+tableName+"'";


        PreparedStatement preparedStatement = connection.prepareStatement(columnDetailsSql);

        ResultSet rs = preparedStatement.executeQuery();
        return processResultSet(rs);
    }

    public SqlQueryResult getTableRawData(Connection connection, String schema, String tableName) throws SQLException {

        String sql = "select * from  " + schema + "." + tableName;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet rs = preparedStatement.executeQuery();
        return processResultSet(rs);


    }

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
                " where u.name = '"+schemaName+"' "+
                "ORDER BY    t.name, c.colorder";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet rs = preparedStatement.executeQuery();
        return processResultSet(rs);

    }

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
}
