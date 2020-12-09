package com.zone5cloud.http.core.api;

import java.io.File;
import java.util.concurrent.Future;

import com.zone5cloud.http.core.AbstractAPI;
import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseHandler;
import com.zone5cloud.core.Types;
import com.zone5cloud.core.activities.Activities;
import com.zone5cloud.core.activities.DataFileUploadContext;
import com.zone5cloud.core.activities.DataFileUploadIndex;
import com.zone5cloud.core.activities.DataFileUploadRecent;
import com.zone5cloud.core.activities.UserWorkoutFileSearch;
import com.zone5cloud.core.activities.UserWorkoutResult;
import com.zone5cloud.core.activities.VActivity;
import com.zone5cloud.core.enums.ActivityResultType;
import com.zone5cloud.core.enums.IntensityZoneType;
import com.zone5cloud.core.enums.RelativePeriod;
import com.zone5cloud.core.search.MappedResult;
import com.zone5cloud.core.search.MappedSearchResult;
import com.zone5cloud.core.search.SearchInput;
import com.zone5cloud.core.search.SearchInputReport;

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
	
	/** 
	 * <p>Upload a completed activity file for processing. ie upload a fit, gpx, srm etc file from a head-unit or watch.</p>
	 * 
	 * <p>See <a href="https://github.com/Zone5-Ventures/z5-sdk-java-core/wiki/Upload-completed-activity">https://github.com/Zone5-Ventures/z5-sdk-java-core/wiki/Upload-completed-activity</a></p>
	 * 
	 */
	public Future<Z5HttpResponse<DataFileUploadIndex>> upload(File file, DataFileUploadContext meta) {
		return upload(file, meta, null);
	}
	
	/** 
	 * <p>Upload a completed activity file for processing. ie upload a fit, gpx, srm etc file from a head-unit or watch.</p>
	 * 
	 * <p>See <a href="https://github.com/Zone5-Ventures/z5-sdk-java-core/wiki/Upload-completed-activity">https://github.com/Zone5-Ventures/z5-sdk-java-core/wiki/Upload-completed-activity</a></p>
	 * 
	 */
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
	public Future<Z5HttpResponse<DataFileUploadIndex>> uploadStatus(long indexId) {
		return uploadStatus(indexId, null);
	}
	
	/** Use the DataFileUploadIndex.id to request the file processing status of an upload */
	public Future<Z5HttpResponse<DataFileUploadIndex>> uploadStatus(long indexId, Z5HttpResponseHandler<DataFileUploadIndex> handler)  {
		String path = Activities.FILE_INDEX_STATUS.replace("{indexId}", String.format("%d", indexId));
		return getClient().doGet(Types.DATAFILE_UPLOAD_INDEX, path, handler);
	}
	
	/** Use the DataFileUploadIndex.id to download the original uploaded file - this can be used even if the file could not be processed  */
	public Future<Z5HttpResponse<File>> downloadUpload(long indexId, File tgt, Z5HttpResponseHandler<File> handler) {
		String path = Activities.FILE_INDEX_DOWNLOAD.replace("{indexId}", String.format("%d", indexId));
		return getClient().doDownload(path, tgt, handler);
	}
	
	/** Use the DataFileUploadIndex.id to cancel a file upload / delete file which has failed to process */
	public Future<Z5HttpResponse<Boolean>> cancelUpload(long indexId, Z5HttpResponseHandler<Boolean> handler)  {
		String path = Activities.FILE_INDEX_CANCEL.replace("{indexId}", String.format("%d", indexId));
		return getClient().doGet(Types.BOOLEAN, path, handler);
	}
	
	/** Use the DataFileUploadIndex.id to request that a failed file be re-processed / re-attempted. If the file has been successfully processed before, this endpoint will re-queue the file for re-processing. */
	public Future<Z5HttpResponse<DataFileUploadIndex>> retryUpload(long indexId, Z5HttpResponseHandler<DataFileUploadIndex> handler)  {
		String path = Activities.FILE_INDEX_RETRY.replace("{indexId}", String.format("%d", indexId));
		return getClient().doGet(Types.DATAFILE_UPLOAD_INDEX, path, handler);
	}
	
	/** Query for currently processing files, and files which have been uploaded or reprocessed within the last X seconds */
	public Future<Z5HttpResponse<DataFileUploadRecent>> uploadStatus(long userId, int secs, Z5HttpResponseHandler<DataFileUploadRecent> handler)  {
		String path = Activities.FILE_INDEX_STATUS_RECENT.replace("{userId}", String.format("%d", userId).replace("{secs}", String.format("%d", secs)));
		return getClient().doGet(Types.DATAFILE_STATUS_INDEX, path, handler);
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
	
	/** Manually update an activity - ie change a name, or override completed metrics */
	public Future<Z5HttpResponse<VActivity>> update(UserWorkoutResult input) {
		return getClient().doPost(Types.ACTIVITY, Activities.UPDATE, input, null);
	}
	
	/** Manually update an activity - ie change a name, or override completed metrics */
	public Future<Z5HttpResponse<VActivity>> update(UserWorkoutResult input, Z5HttpResponseHandler<VActivity> handler) {
		return getClient().doPost(Types.ACTIVITY, Activities.UPDATE, input, handler);
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
	
	/** Set the specialized bike (bikeId) for a completed activity - Specialized featureset only */
	public Future<Z5HttpResponse<Boolean>> setBikeId(ActivityResultType activityType, long activityId, String bikeId, Z5HttpResponseHandler<Boolean> handler) {
		String path = Activities.SET_BIKE.replace("{activityType}", activityType.name()).replace("{activityId}", String.format("%d", activityId)).replace("{bikeId}", bikeId);
		return getClient().doGet(Types.BOOLEAN, path, handler);
	}
	
	/** Remove the specialized bike used for a completed activity - Specialized featureset only */
	public Future<Z5HttpResponse<Boolean>> removeBikeId(ActivityResultType activityType, long activityId, Z5HttpResponseHandler<Boolean> handler) {
		String path = Activities.REM_BIKE.replace("{activityType}", activityType.name()).replace("{activityId}", String.format("%d", activityId));
		return getClient().doGet(Types.BOOLEAN, path, handler);
	}
}
