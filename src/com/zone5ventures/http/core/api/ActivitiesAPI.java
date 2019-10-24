package com.zone5ventures.http.core.api;

import java.io.File;
import java.util.concurrent.Future;

import com.zone5ventures.common.Types;
import com.zone5ventures.common.activities.Activities;
import com.zone5ventures.common.activities.DataFileUploadContext;
import com.zone5ventures.common.activities.DataFileUploadIndex;
import com.zone5ventures.common.activities.UserWorkoutFileSearch;
import com.zone5ventures.common.activities.UserWorkoutResult;
import com.zone5ventures.common.enums.ActivityResultType;
import com.zone5ventures.common.enums.IntensityZoneType;
import com.zone5ventures.common.enums.RelativePeriod;
import com.zone5ventures.common.search.MappedResult;
import com.zone5ventures.common.search.MappedSearchResult;
import com.zone5ventures.common.search.SearchInput;
import com.zone5ventures.common.search.SearchInputReport;
import com.zone5ventures.http.core.AbstractAPI;
import com.zone5ventures.http.core.responses.Z5HttpResponse;
import com.zone5ventures.http.core.responses.Z5HttpResponseHandler;

public class ActivitiesAPI extends AbstractAPI {
	
	public Future<Z5HttpResponse<MappedSearchResult<UserWorkoutResult>>> search(SearchInput<UserWorkoutFileSearch> search, int offset, int count) {
		return search(search, offset, count, null);
	}
		
	public Future<Z5HttpResponse<MappedSearchResult<UserWorkoutResult>>> search(SearchInput<UserWorkoutFileSearch> search, int offset, int count, Z5HttpResponseHandler<MappedSearchResult<UserWorkoutResult>> handler) {
		String path = Activities.SEARCH.replace("{offset}", String.format("%d", offset)).replace("{count}", String.format("%d", count));
		return getClient().doPost(Types.SEARCH_RESULT_ACTIVITIES, path, search, handler);
	}
	
	public Future<Z5HttpResponse<MappedSearchResult<UserWorkoutResult>>> next(int offset, int count) {
		return next(offset, count, null);
	}
		
	public Future<Z5HttpResponse<MappedSearchResult<UserWorkoutResult>>> next(int offset, int count, Z5HttpResponseHandler<MappedSearchResult<UserWorkoutResult>> handler) {
		String path = Activities.NEXT.replace("{offset}", String.format("%d", offset)).replace("{count}", String.format("%d", count));
		return getClient().doGet(Types.SEARCH_RESULT_ACTIVITIES, path, handler);
	}
	
	/** Upload a completed activity file - ie a fit, gpx, tcx, act, srm etc */
	public Future<Z5HttpResponse<DataFileUploadIndex>> upload(File file, DataFileUploadContext meta) {
		return upload(file, meta, null);
	}
	
	public Future<Z5HttpResponse<DataFileUploadIndex>> upload(File file, DataFileUploadContext meta, Z5HttpResponseHandler<DataFileUploadIndex> handler) {
		return getClient().doUpload(Types.DATAFILE_UPLOAD_INDEX, Activities.UPLOAD, meta, file, handler);
	}
	
	/** Download the original uploaded file */
	public Future<Z5HttpResponse<File>> downloadOriginal(long fileId, File tgt) {
		return downloadOriginal(fileId, tgt, null);
	}
	
	/** Download the original uploaded file */
	public Future<Z5HttpResponse<File>> downloadOriginal(long fileId, File tgt, Z5HttpResponseHandler<File> handler) {
		String path = Activities.DOWNLOAD_FIT.replace("{fileId}", String.format("%d", fileId));
		return getClient().doDownload(path, tgt, handler);
	}
	
	/** Download the a normalized FIT file which contains typed numeric data channels - use this for timeseries graphs or raw channel analysis */
	public Future<Z5HttpResponse<File>> downloadRaw(long fileId, File tgt) {
		return downloadRaw(fileId, tgt, null);
	}
	
	/** Download the a normalized FIT file which contains typed numeric data channels - use this for timeseries graphs or raw channel analysis */
	public Future<Z5HttpResponse<File>> downloadRaw(long fileId, File tgt, Z5HttpResponseHandler<File> handler) {
		String path = Activities.DOWNLOAD_RAW3.replace("{fileId}", String.format("%d", fileId));
		return getClient().doDownload(path, tgt, handler);
	}
	
	/** Download the normalized CSV file */
	public Future<Z5HttpResponse<File>> downloadCsv(long fileId, File tgt) {
		return downloadCsv(fileId, tgt, null);
	}
	
	/** Download the normalized CSV file */
	public Future<Z5HttpResponse<File>> downloadCsv(long fileId, File tgt, Z5HttpResponseHandler<File> handler) {
		String path = Activities.DOWNLOAD_CSV.replace("{fileId}", String.format("%d", fileId));
		return getClient().doDownload(path, tgt, handler);
	}
	
	/** Download the map png file */
	public Future<Z5HttpResponse<File>> downloadMap(long fileId, File tgt) {
		return downloadMap(fileId, tgt, null);
	}
	
	/** Download the map png file */
	public Future<Z5HttpResponse<File>> downloadMap(long fileId, File tgt, Z5HttpResponseHandler<File> handler) {
		String path = Activities.DOWNLOAD_MAP.replace("{fileId}", String.format("%d", fileId));
		return getClient().doDownload(path, tgt, handler);
	}
	
	/** Use the DataFileUploadIndex.id to request the file processing status of an upload */
	public Future<Z5HttpResponse<DataFileUploadIndex>> getUploadStatus(long indexId) {
		return getUploadStatus(indexId, null);
	}
	
	/** Use the DataFileUploadIndex.id to request the file processing status of an upload */
	public Future<Z5HttpResponse<DataFileUploadIndex>> getUploadStatus(long indexId, Z5HttpResponseHandler<DataFileUploadIndex> handler)  {
		String path = Activities.FILE_INDEX_STATUS.replace("{indexId}", String.format("%d", indexId));
		return getClient().doGet(Types.DATAFILE_UPLOAD_INDEX, path, handler);
	}

	/** Delete a file, workout, event by id */
	public Future<Z5HttpResponse<Boolean>> delete(ActivityResultType activityType, long activityId) {
		return delete(activityType, activityId, null);
	}
	
	/** Delete a file, workout, event by id */
	public Future<Z5HttpResponse<Boolean>> delete(ActivityResultType activityType, long activityId, Z5HttpResponseHandler<Boolean> handler) {
		String path = Activities.DELETE.replace("{activityType}", activityType.name()).replace("{activityId}", String.format("%d", activityId));
		return getClient().doGet(Types.BOOLEAN, path, handler);
	}
	
	public Future<Z5HttpResponse<MappedResult<UserWorkoutResult>>> timeInZones(ActivityResultType activityType, long activityId, IntensityZoneType zoneType) {
		return timeInZones(activityType, activityId, zoneType, null);
	}
	
	public Future<Z5HttpResponse<MappedResult<UserWorkoutResult>>> timeInZones(ActivityResultType activityType, long activityId, IntensityZoneType zoneType, Z5HttpResponseHandler<MappedResult<UserWorkoutResult>> handler) {
		SearchInputReport input = Activities.newInstance(activityType, activityId);
		String path = Activities.TIME_IN_ZONE.replace("{zoneType}", zoneType.name());
		return getClient().doPost(Types.MAPPED_RESULT_ACTIVITIES, path, input, handler);
	}
	
	public Future<Z5HttpResponse<MappedResult<UserWorkoutResult>>> powercurve(ActivityResultType activityType, long activityId, RelativePeriod referencePeriod) {
		return powercurve(activityType, activityId, referencePeriod, null);
	}
	/** Get the peak power curve for this activity, and include a reference series. */
	public Future<Z5HttpResponse<MappedResult<UserWorkoutResult>>> powercurve(ActivityResultType activityType, long activityId, RelativePeriod referencePeriod, Z5HttpResponseHandler<MappedResult<UserWorkoutResult>> handler) {
		SearchInputReport input = Activities.newInstancePeaksCurve(activityType, activityId, referencePeriod);
		return getClient().doPost(Types.MAPPED_RESULT_ACTIVITIES, Activities.PEAK_POWER, input, handler);
	}
	
	/** Get the peak heart rate curve for this activity, and include a reference series. */
	public Future<Z5HttpResponse<MappedResult<UserWorkoutResult>>> heartcurve(ActivityResultType activityType, long activityId, RelativePeriod referencePeriod, Z5HttpResponseHandler<MappedResult<UserWorkoutResult>> handler) {
		SearchInputReport input = Activities.newInstancePeaksCurve(activityType, activityId, referencePeriod);
		return getClient().doPost(Types.MAPPED_RESULT_ACTIVITIES, Activities.PEAK_HEARTRATE, input, handler);
	}
	
	/** Get the peak w/kg curve for this activity, and include a reference series. */
	public Future<Z5HttpResponse<MappedResult<UserWorkoutResult>>> wkgcurve(ActivityResultType activityType, long activityId, RelativePeriod referencePeriod, Z5HttpResponseHandler<MappedResult<UserWorkoutResult>> handler) {
		SearchInputReport input = Activities.newInstancePeaksCurve(activityType, activityId, referencePeriod);
		return getClient().doPost(Types.MAPPED_RESULT_ACTIVITIES, Activities.PEAK_WKG, input, handler);
	}
	
	/** Get the peak pace curve for this activity, and include a reference series. */
	public Future<Z5HttpResponse<MappedResult<UserWorkoutResult>>> pacecurve(ActivityResultType activityType, long activityId, RelativePeriod referencePeriod, Z5HttpResponseHandler<MappedResult<UserWorkoutResult>> handler) {
		SearchInputReport input = Activities.newInstancePeaksCurve(activityType, activityId, referencePeriod);
		return getClient().doPost(Types.MAPPED_RESULT_ACTIVITIES, Activities.PEAK_PACE, input, handler);
	}
	
	/** Get the peak leg spring stiffness curve for this activity, and include a reference series. */
	public Future<Z5HttpResponse<MappedResult<UserWorkoutResult>>>  lsscurve(ActivityResultType activityType, long activityId, RelativePeriod referencePeriod, Z5HttpResponseHandler<MappedResult<UserWorkoutResult>> handler) {
		SearchInputReport input = Activities.newInstancePeaksCurve(activityType, activityId, referencePeriod);
		return getClient().doPost(Types.MAPPED_RESULT_ACTIVITIES, Activities.PEAK_LSS, input, handler);
	}
	
	/** Get the peak leg spring stiffness / kg for this activity, and include a reference series. */
	public Future<Z5HttpResponse<MappedResult<UserWorkoutResult>>> lsskgcurve(ActivityResultType activityType, long activityId, RelativePeriod referencePeriod, Z5HttpResponseHandler<MappedResult<UserWorkoutResult>> handler) {
		SearchInputReport input = Activities.newInstancePeaksCurve(activityType, activityId, referencePeriod);
		return getClient().doPost(Types.MAPPED_RESULT_ACTIVITIES, Activities.PEAK_LSSKG, input, handler);
	}
}
