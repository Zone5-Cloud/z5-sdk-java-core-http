package com.zone5cloud.http.core.api;

import java.util.List;
import java.util.concurrent.Future;

import com.zone5cloud.http.core.AbstractAPI;
import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseHandler;
import com.zone5cloud.core.Types;
import com.zone5cloud.core.activities.Activities;
import com.zone5cloud.core.activities.UserWorkoutResultAggregates;
import com.zone5cloud.core.enums.ActivityType;
import com.zone5cloud.core.search.DateRange;
import com.zone5cloud.core.search.MappedResult;
import com.zone5cloud.core.search.SearchInput;
import com.zone5cloud.core.search.SearchInputReport;

public class MetricsAPI extends AbstractAPI {
	
	/**
	 * Get aggregate metrics for a given set of users and date ranges.<br>
	 * Supported aggregates include;
	 * <ol>
	 * <li>avg - simple average
	 * <li>min - minimum value
	 * <li>max - maximim value
	 * <li>wavg - weighted average (weighted by time)
	 * <li>sum - sum of values
	 * </ol>
	 * @param sport - required - the sport type
	 * @param userIds - required - 1 or more userIds can be requested
	 * @param ranges - the date ranges - 1 or more ranges can be requested. If the ranges overlap it is indeterministic which range the metrics will be included in.
	 * @param fields - the aggregate fields being requested
	 * 
	 */
	public Future<Z5HttpResponse<MappedResult<UserWorkoutResultAggregates>>> get(ActivityType sport, List<Long> userIds, List<DateRange> ranges, List<String> fields) {
		return get(sport, userIds, ranges, fields);
	}
	
	public Future<Z5HttpResponse<MappedResult<UserWorkoutResultAggregates>>> get(ActivityType sport, List<Long> userIds, List<DateRange> ranges, List<String> fields, Z5HttpResponseHandler<MappedResult<UserWorkoutResultAggregates>> handler) {
		SearchInput<SearchInputReport> input = Activities.newInstanceMetrics(sport, userIds, ranges, fields);
		return getClient().doPost(Types.MAPPED_RESULT_AGGREGATES, Activities.METRICS, input, handler);
	}
	
	
}