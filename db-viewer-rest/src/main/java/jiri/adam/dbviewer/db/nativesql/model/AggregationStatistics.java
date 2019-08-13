package jiri.adam.dbviewer.db.nativesql.model;

import lombok.Data;

import java.util.List;

@Data
public class AggregationStatistics {

    private List<TableAggregationStatistics> aggregationStatistics;
}
