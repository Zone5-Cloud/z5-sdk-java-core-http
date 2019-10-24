package com.zone5ventures.http.core.api;

import java.io.File;
import java.util.concurrent.Future;

import com.zone5ventures.core.Types;
import com.zone5ventures.core.routes.Routes;
import com.zone5ventures.core.routes.UserRoute;
import com.zone5ventures.core.routes.UserRouteOutputType;
import com.zone5ventures.core.routes.UserRouteSearch;
import com.zone5ventures.core.search.SearchResult;
import com.zone5ventures.http.core.AbstractAPI;
import com.zone5ventures.http.core.responses.Z5HttpResponse;
import com.zone5ventures.http.core.responses.Z5HttpResponseHandler;

public class RoutesAPI extends AbstractAPI {
	
	/** Search for routes */
	public Future<Z5HttpResponse<SearchResult<UserRoute>>> search(UserRouteSearch search, int offset, int count) {
		return search(search, offset, count, null);
	}
		
	/** Search for routes */
	public Future<Z5HttpResponse<SearchResult<UserRoute>>> search(UserRouteSearch search, int offset, int count, Z5HttpResponseHandler<SearchResult<UserRoute>> handler) {
		String path = Routes.SEARCH.replace("{offset}", String.format("%d", offset)).replace("{count}", String.format("%d", count));
		return getClient().doPost(Types.SEARCH_RESULT_ROUTES, path, search, handler);
	}
	
	/** Get the next paginated set from the previous search */
	public Future<Z5HttpResponse<SearchResult<UserRoute>>> next(int offset, int count) {
		return next(offset, count, null);
	}
		
	/** Get the next paginated set from the previous search */
	public Future<Z5HttpResponse<SearchResult<UserRoute>>> next(int offset, int count, Z5HttpResponseHandler<SearchResult<UserRoute>> handler) {
		String path = Routes.NEXT.replace("{offset}", String.format("%d", offset)).replace("{count}", String.format("%d", count));
		return getClient().doGet(Types.SEARCH_RESULT_ROUTES, path, handler);
	}
	
	/** Get a summary view of a route (just route meta-data - does not include raw points */
	public Future<Z5HttpResponse<UserRoute>> summary(long routeId) {
		return summary(routeId, null);
	}
	
	/** Get a summary view of a route (just route meta-data - does not include raw points */
	public Future<Z5HttpResponse<UserRoute>> summary(long routeId, Z5HttpResponseHandler<UserRoute> handler) {
		String path = Routes.SUMMARY.replace("{routeId}", String.format("%d", routeId));
		return getClient().doGet(Types.RESULT_ROUTE, path, handler);
	}
	
	/** Get a summary view of a route (just route meta-data - does not include raw points */
	public Future<Z5HttpResponse<UserRoute>> summary(String uuid) {
		return summary(uuid, null);
	}
	
	/** Get a summary view of a route (just route meta-data - does not include raw points */
	public Future<Z5HttpResponse<UserRoute>> summary(String uuid, Z5HttpResponseHandler<UserRoute> handler) {
		String path = Routes.SUMMARY.replace("{routeId}", String.format("%s", uuid));
		return getClient().doGet(Types.RESULT_ROUTE, path, handler);
	}
	
	/** Get a detailed view of a route (just route meta-data - does not include raw points */
	public Future<Z5HttpResponse<UserRoute>> detailed(long routeId) {
		return detailed(routeId, null);
	}
	
	/** Get a detailed view of a route (just route meta-data - does not include raw points */
	public Future<Z5HttpResponse<UserRoute>> detailed(long routeId, Z5HttpResponseHandler<UserRoute> handler) {
		String path = Routes.DETAILED.replace("{routeId}", String.format("%d", routeId));
		return getClient().doGet(Types.RESULT_ROUTE, path, handler);
	}
	
	/** Get a detailed view of a route (just route meta-data - does not include raw points */
	public Future<Z5HttpResponse<UserRoute>> detailed(String uuid) {
		return detailed(uuid, null);
	}
	
	/** Get a detailed view of a route (just route meta-data - does not include raw points */
	public Future<Z5HttpResponse<UserRoute>> detailed(String uuid, Z5HttpResponseHandler<UserRoute> handler) {
		String path = Routes.DETAILED.replace("{routeId}", String.format("%s", uuid));
		return getClient().doGet(Types.RESULT_ROUTE, path, handler);
	}
	
	/** Delete a route */
	public Future<Z5HttpResponse<Boolean>> delete(long routeId) {
		return delete(routeId, null);
	}
	
	/** Delete a route */
	public Future<Z5HttpResponse<Boolean>> delete(long routeId, Z5HttpResponseHandler<Boolean> handler) {
		String path = Routes.DELETE.replace("{routeId}", String.format("%d", routeId));
		return getClient().doGet(Types.BOOLEAN, path, handler);
	}
	
	/** Download a png map image of the route */
	public Future<Z5HttpResponse<File>> png(long routeId, File tgt) {
		return png(routeId, tgt);
	}
	
	/** Download a png map image of the route */
	public Future<Z5HttpResponse<File>> png(long routeId, File tgt, Z5HttpResponseHandler<File> handler) {
		String path = Routes.DOWNLOAD_PNG.replace("{routeId}", String.format("%d", routeId));
		return getClient().doDownload(path, tgt, handler);
	}
	
	/** Download a png map image of the route */
	public Future<Z5HttpResponse<File>> png(String uuid, File tgt) {
		return png(uuid, tgt);
	}
	
	/** Download a png map image of the route */
	public Future<Z5HttpResponse<File>> png(String uuid, File tgt, Z5HttpResponseHandler<File> handler) {
		String path = Routes.DOWNLOAD_PNG.replace("{routeId}", String.format("%s", uuid));
		return getClient().doDownload(path, tgt, handler);
	}
	
	/** Download a fit version of the route - this includes all data points (location, elevation, elapsed distance etc), directions (if any) and course point markers */
	public Future<Z5HttpResponse<File>> fit(long routeId, File tgt) {
		return fit(routeId, tgt);
	}
	
	/** Download a fit version of the route - this includes all data points (location, elevation, elapsed distance etc), directions (if any) and course point markers */
	public Future<Z5HttpResponse<File>> fit(long routeId, File tgt, Z5HttpResponseHandler<File> handler) {
		String path = Routes.DOWNLOAD_FIT.replace("{routeId}", String.format("%d", routeId));
		return getClient().doDownload(path, tgt, handler);
	}
	
	/** Download a fit version of the route - this includes all data points (location, elevation, elapsed distance etc), directions (if any) and course point markers */
	public Future<Z5HttpResponse<File>> fit(String uuid, File tgt) {
		return fit(uuid, tgt);
	}
	
	/** Download a fit version of the route - this includes all data points (location, elevation, elapsed distance etc), directions (if any) and course point markers */
	public Future<Z5HttpResponse<File>> fit(String uuid, File tgt, Z5HttpResponseHandler<File> handler) {
		String path = Routes.DOWNLOAD_FIT.replace("{routeId}", String.format("%s", uuid));
		return getClient().doDownload(path, tgt, handler);
	}
	
	
	/** Download a gpx version of the route */
	public Future<Z5HttpResponse<File>> gpx(long routeId, File tgt) {
		return gpx(routeId, tgt);
	}
	
	/** Download a gpx version of the route */
	public Future<Z5HttpResponse<File>> gpx(long routeId, File tgt, Z5HttpResponseHandler<File> handler) {
		String path = Routes.DOWNLOAD_GPX.replace("{routeId}", String.format("%d", routeId));
		return getClient().doDownload(path, tgt, handler);
	}
	
	/** Download a gpx version of the route */
	public Future<Z5HttpResponse<File>> gpx(String uuid, File tgt) {
		return gpx(uuid, tgt);
	}
	
	/** Download a gpx version of the route */
	public Future<Z5HttpResponse<File>> gpx(String uuid, File tgt, Z5HttpResponseHandler<File> handler) {
		String path = Routes.DOWNLOAD_GPX.replace("{routeId}", String.format("%s", uuid));
		return getClient().doDownload(path, tgt, handler);
	}
	
	
	/** Update the meta-data of this route - ie set equipment, terrain, name, description, tags etc. Partial/shallow updates are supported */
	public Future<Z5HttpResponse<Boolean>> update(long routeId, UserRoute route, Z5HttpResponseHandler<Boolean> handler) {
		String path = Routes.UPDATE.replace("{routeId}", String.format("%d", routeId));
		return getClient().doPost(Types.BOOLEAN, path, route, handler);
	}
	
	/** Create a new route - route is the met-data and json is a json formatted route file;<br>
	 * 
	 * The json file has a structure of;
	 * <p>
	 * {
	 *   "course_points": [ [ -420945877,1778923344,4572,0,"Head northeast on Lady Denman Dr<br>Walk your bicycle"], ... ],
	 *   "records": [[-420751495,1779320598,0,0,0], ...]
	 * }
	 * </p>
	 * <br>
	 * Course points - lat, lng, elapsed distance (m), fit course point type (ordinal), text
	 * <br>
	 * Records - lat, lng, elapsed distance (m), altitude (m), grade (%)
	 * <br>
	 * */
	public Future<Z5HttpResponse<UserRoute>> createFromJsonFile(UserRoute route, File json) {
		return createFromJsonFile(route, json, null);
	}
	
	public Future<Z5HttpResponse<UserRoute>> createFromJsonFile(UserRoute route, File json, Z5HttpResponseHandler<UserRoute> handler) {
		String path = Routes.UPLOAD.replace("{format}", UserRouteOutputType.js.name());
		return getClient().doUpload(Types.RESULT_ROUTE, path, route, json, handler);
	}
	
	/** Create a new route from the given fit file */
	public Future<Z5HttpResponse<UserRoute>> createFromFitFile(UserRoute route, File fit) {
		return createFromFitFile(route, fit, null);
	}
	
	/** Create a new route from the given fit file */
	public Future<Z5HttpResponse<UserRoute>> createFromFitFile(UserRoute route, File fit, Z5HttpResponseHandler<UserRoute> handler) {
		String path = Routes.UPLOAD.replace("{format}", UserRouteOutputType.fit.name());
		return getClient().doUpload(Types.RESULT_ROUTE, path, route, fit, handler);
	}
	
	/** Update a route from the given json data */
	public Future<Z5HttpResponse<UserRoute>> updateFromJsonFile(long routeId, UserRoute route, File json) {
		return createFromJsonFile(route, json, null);
	}
	
	/** Update a route from the given json data */
	public Future<Z5HttpResponse<UserRoute>> updateFromJsonFile(long routeId, UserRoute route, File json, Z5HttpResponseHandler<UserRoute> handler) {
		String path = Routes.UPLOAD_UPDATE.replace("{format}", UserRouteOutputType.js.name()).replace("{routeId}", String.format("%d", routeId));
		return getClient().doUpload(Types.RESULT_ROUTE, path, route, json, handler);
	}
	
	/** Update a route from the given fit data */
	public Future<Z5HttpResponse<UserRoute>> updateFromFitFile(long routeId, UserRoute route, File fit) {
		return createFromFitFile(route, fit, null);
	}
	
	/** Update a route from the given fit data */
	public Future<Z5HttpResponse<UserRoute>> updateFromFitFile(long routeId, UserRoute route, File fit, Z5HttpResponseHandler<UserRoute> handler) {
		String path = Routes.UPLOAD_UPDATE.replace("{format}", UserRouteOutputType.fit.name()).replace("{routeId}", String.format("%d", routeId));
		return getClient().doUpload(Types.RESULT_ROUTE, path, route, fit, handler);
	}
	
	/** Create a new route from an existing fileId / completed activity - note that actual conversation occurs asynchronously. The route will be available once it has been processed. */
	public Future<Z5HttpResponse<UserRoute>> createFromActivity(long fileId) {
		return createFromActivity(fileId, null);
	}
	
	/** Create a new route from an existing fileId / completed activity - note that actual conversation occurs asynchronously. The route will be available once it has been processed. */
	public Future<Z5HttpResponse<UserRoute>> createFromActivity(long fileId, Z5HttpResponseHandler<UserRoute> handler) {
		String path = Routes.CLONE_ACTIVITY.replace("{fileId}", String.format("%d", fileId));
		return getClient().doGet(Types.RESULT_ROUTE, path, handler);
	}
	
	/** Create a new route from an existing route - note that actual conversation occurs asynchronously. The route will be available once it has been processed. */
	public Future<Z5HttpResponse<UserRoute>> createFromRoute(long routeId) {
		return createFromRoute(routeId, null);
	}
	
	/** Create a new route from an existing route - note that actual conversation occurs asynchronously. The route will be available once it has been processed. */
	public Future<Z5HttpResponse<UserRoute>> createFromRoute(long routeId, Z5HttpResponseHandler<UserRoute> handler) {
		String path = Routes.CLONE_ROUTE.replace("{routeId}", String.format("%d", routeId));
		return getClient().doGet(Types.RESULT_ROUTE, path, handler);
	}
}