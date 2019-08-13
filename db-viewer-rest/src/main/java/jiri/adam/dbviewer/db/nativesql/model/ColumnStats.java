package jiri.adam.dbviewer.db.nativesql.model;

import lombok.Data;

/**
 * Hold one column's stats
 */
@Data
public class ColumnStats {
    private String columnName;
    private Long min;
    private Long rowsCount;
    private Long max;
    private Double avg;
    private Long median;
}
