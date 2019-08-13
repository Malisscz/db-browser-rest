package jiri.adam.dbviewer.db.nativesql.model;

import lombok.Data;

import java.util.List;

@Data
public class TableAggregationStatistics {
    String tableName;
    Long rowsCount;
    Integer columnsCount;
    List<ColumnStats> columnStats;
}
