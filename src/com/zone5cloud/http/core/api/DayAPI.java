package com.zone5cloud.http.core.api;

import java.util.concurrent.Future;

import com.zone5cloud.http.core.AbstractAPI;
import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseHandler;
import com.zone5cloud.core.Types;
import com.zone5cloud.core.activities.Activities;
import com.zone5cloud.core.day.UserDay;
import com.zone5cloud.core.day.UserDaySearch;
import com.zone5cloud.core.search.MappedSearchResult;
import com.zone5cloud.core.search.SearchInput;

public class DayAPI extends AbstractAPI {
	
	public Future<Z5HttpResponse<MappedSearchResult<UserDay>>> search(SearchInput<UserDaySearch> search, int offset, int count) {
		return search(search, offset, count, null);
	}
		
	public Future<Z5HttpResponse<MappedSearchResult<UserDay>>> search(SearchInput<UserDaySearch> search, int offset, int count, Z5HttpResponseHandler<MappedSearchResult<UserDay>> handler) {
		String path = Activities.SEARCH.replace("{offset}", String.format("%d", offset)).replace("{count}", String.format("%d", count));
		return getClient().doPost(Types.SEARCH_RESULT_DAY, path, search, handler);
	}
	
	public Future<Z5HttpResponse<MappedSearchResult<UserDay>>> next(int offset, int count) {
		return next(offset, count, null);
	}
		
	public Future<Z5HttpResponse<MappedSearchResult<UserDay>>> next(int offset, int count, Z5HttpResponseHandler<MappedSearchResult<UserDay>> handler) {
		String path = Activities.NEXT.replace("{offset}", String.format("%d", offset)).replace("{count}", String.format("%d", count));
		return getClient().doGet(Types.SEARCH_RESULT_DAY, path, handler);
	}
}
