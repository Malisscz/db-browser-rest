package jiri.adam.dbviewer.db.nativesql.model;

import lombok.Data;

import java.util.List;

/**
 * Wrapper for List of aggretation stats
 */
@Data
public class AggregationStatistics {

    private List<TableAggregationStatistics> aggregationStatistics;
}
