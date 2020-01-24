package com.zone5ventures.http.core.api;

import java.io.File;
import java.util.concurrent.Future;

import com.zone5ventures.core.Types;
import com.zone5ventures.core.ride.UserScheduledActivities;
import com.zone5ventures.core.ride.UserScheduledActivity;
import com.zone5ventures.core.ride.UserScheduledActivitySearch;
import com.zone5ventures.core.search.SearchResult;
import com.zone5ventures.http.core.AbstractAPI;
import com.zone5ventures.http.core.responses.Z5HttpResponse;
import com.zone5ventures.http.core.responses.Z5HttpResponseHandler;

/** 
 * This set of endpoints is used for scheduling and searching for group rides
 * 
 * Group rides are represented by the UserScheduledActivity object.
 *  
 *  */
public class RideAPI extends AbstractAPI {
	
	public Future<Z5HttpResponse<SearchResult<UserScheduledActivity>>> search(UserScheduledActivitySearch search, int offset, int count) {
		return search(search, offset, count, null);
	}
		
	public Future<Z5HttpResponse<SearchResult<UserScheduledActivity>>> search(UserScheduledActivitySearch search, int offset, int count, Z5HttpResponseHandler<SearchResult<UserScheduledActivity>> handler) {
		String path = UserScheduledActivities.SEARCH.replace("{offset}", String.format("%d", offset)).replace("{count}", String.format("%d", count));
		return getClient().doPost(Types.SEARCH_RESULT_RIDES, path, search, handler);
	}
	
	public Future<Z5HttpResponse<SearchResult<UserScheduledActivity>>> next(int offset, int count) {
		return next(offset, count, null);
	}
		
	public Future<Z5HttpResponse<SearchResult<UserScheduledActivity>>> next(int offset, int count, Z5HttpResponseHandler<SearchResult<UserScheduledActivity>> handler) {
		String path = UserScheduledActivities.NEXT.replace("{offset}", String.format("%d", offset)).replace("{count}", String.format("%d", count));
		return getClient().doGet(Types.SEARCH_RESULT_RIDES, path, handler);
	}
	
	public Future<Z5HttpResponse<UserScheduledActivity>> add(UserScheduledActivity ride) {
		return add(ride, null);
	}
	
	public Future<Z5HttpResponse<UserScheduledActivity>> add(UserScheduledActivity ride, Z5HttpResponseHandler<UserScheduledActivity> handler) {
		String path = UserScheduledActivities.ADD;
		// Post or Put
		return getClient().doPost(Types.RESULT_RIDE, path, ride, handler);
	}
	
	public Future<Z5HttpResponse<UserScheduledActivity>> getSummary(long id) {
		return getSummary(id, null);
	}
	
	public Future<Z5HttpResponse<UserScheduledActivity>> getSummary(long id, Z5HttpResponseHandler<UserScheduledActivity> handler) {
		String path = UserScheduledActivities.GET.replace("{id}", String.format("%d", id));
		return getClient().doGet(Types.RESULT_RIDE, path, handler);
	}
	
	public Future<Z5HttpResponse<UserScheduledActivity>> getDetailed(long id) {
		return getDetailed(id, null);
	}
	
	public Future<Z5HttpResponse<UserScheduledActivity>> getDetailed(long id, Z5HttpResponseHandler<UserScheduledActivity> handler) {
		String path = UserScheduledActivities.GET_DETAILED.replace("{id}", String.format("%d", id));
		return getClient().doGet(Types.RESULT_RIDE, path, handler);
	}
	
	public Future<Z5HttpResponse<Boolean>> delete(long id) {
		return delete(id, null);
	}
	
	public Future<Z5HttpResponse<Boolean>> delete(long id, Z5HttpResponseHandler<Boolean> handler) {
		String path = UserScheduledActivities.DELETE.replace("{id}", String.format("%d", id));
		return getClient().doDelete(Types.BOOLEAN, path, handler);
	}
	
	/** Download a combined ride/route map - valid for rides with routes */
	public Future<Z5HttpResponse<File>> downloadMap(long id, File tgt) {
		return downloadMap(id, tgt, null);
	}
	
	/** Download a combined ride/route map - valid for rides with routes */
	public Future<Z5HttpResponse<File>> downloadMap(long id, File tgt, Z5HttpResponseHandler<File> handler) {
		String path = UserScheduledActivities.MAP.replace("{id}", String.format("%d", id));
		return getClient().doDownload(path, tgt, handler);
	}
	

	public Future<Z5HttpResponse<String>> uploadImage(long id, String imageField, File image) {
		return uploadImage(id, imageField, image, null);
	}
	
	/** Download a combined ride/route map - valid for rides with routes */
	public Future<Z5HttpResponse<String>> uploadImage(long id, String imageField, File image, Z5HttpResponseHandler<String> handler) {
		String path = UserScheduledActivities.UPLOAD_IMAGE.replace("{id}", String.format("%d", id).replace("{field}", imageField));
		return getClient().doUpload(Types.STRING, path, null, image, handler);
	}
	
	public Future<Z5HttpResponse<Boolean>> deleteImage(long id, String imageField) {
		return deleteImage(id, imageField, null);
	}
	
	public Future<Z5HttpResponse<Boolean>> deleteImage(long id, String imageField, Z5HttpResponseHandler<Boolean> handler) {
		String path = UserScheduledActivities.UPLOAD_IMAGE.replace("{id}", String.format("%d", id).replace("{field}", imageField));
		return getClient().doDelete(Types.BOOLEAN, path, handler);
	}
}
