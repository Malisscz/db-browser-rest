package jiri.adam.dbviewer.db.nativesql.model;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * Generic SQL Query result with column names and data rows
 */
@Data
public class SqlQueryResult {

    List<String> columnNames = new LinkedList<>();
    List<List<Object>> rows = new LinkedList<>();
}
