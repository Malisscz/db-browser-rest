package jiri.adam.dbviewer.db.nativesql.model;

import lombok.Data;

import java.util.List;

/**
 * Pojo for holding result of one table's statistics
 */
@Data
public class TableAggregationStatistics {
    String tableName;
    Long rowsCount;
    Integer columnsCount;
    List<ColumnStats> columnStats;
}
