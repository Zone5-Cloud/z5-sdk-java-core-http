package com.zone5cloud.http.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import com.zone5cloud.core.activities.DataFileUploadContext;
import com.zone5cloud.core.activities.DataFileUploadIndex;
import com.zone5cloud.core.activities.UserWorkoutFileSearch;
import com.zone5cloud.core.activities.UserWorkoutResult;
import com.zone5cloud.core.activities.VActivity;
import com.zone5cloud.core.enums.ActivityResultType;
import com.zone5cloud.core.enums.ActivityType;
import com.zone5cloud.core.enums.Equipment;
import com.zone5cloud.core.enums.FileUploadState;
import com.zone5cloud.core.enums.IntensityZoneType;
import com.zone5cloud.core.enums.RelativePeriod;
import com.zone5cloud.core.enums.UserWorkoutState;
import com.zone5cloud.core.enums.WorkoutType;
import com.zone5cloud.core.search.DateRange;
import com.zone5cloud.core.search.MappedResult;
import com.zone5cloud.core.search.MappedSearchResult;
import com.zone5cloud.core.search.Order;
import com.zone5cloud.core.search.SearchInput;
import com.zone5cloud.http.core.api.ActivitiesAPI;

public class TestActivitiesAPI extends BaseTest {

	ActivitiesAPI api = new ActivitiesAPI();
	
	@Before
	public void setup() throws InterruptedException, ExecutionException {
		login();
	}
	
	@Test
	public void testUploadWithNoMetadata() throws Exception {
		// a completed ride file
		File fit = new File("../z5-sdk-java-core/test-resources/2013-12-22-10-30-12.fit");
		assertTrue(fit.exists());
		
		// Upload the file
		DataFileUploadIndex r = api.upload(fit, null).get().getResult();
		if (r.getState() == FileUploadState.finished || (r.getState() == FileUploadState.error && r.getResultId() != null)) {
			assertTrue(api.delete(ActivityResultType.files, r.getResultId()).get().getResult());
			return;
		}
		assertNotNull(r.getId()); // file processing index id
		assertTrue(r.getState() == FileUploadState.pending || r.getState() == FileUploadState.queued);
		
		// Wait for it to process
		while(r.getState() != FileUploadState.finished) {
			Thread.sleep(1000L);
			r = api.uploadStatus(r.getId()).get().getResult();
		}
		
		assertNotNull(r.getResultId());
		
		File f = api.downloadOriginal(r.getResultId(), createTempFile("fit")).get().getResult();
		assertTrue(f.exists() && f.length() == fit.length());
		
		f = api.downloadMap(r.getResultId(), createTempFile("fit")).get().getResult();
		assertTrue(f.exists() && f.length() > 0);	
		
		f = api.downloadRaw(r.getResultId(), createTempFile("fit")).get().getResult();
		assertTrue(f.exists() && f.length() > 0);
		
		f = api.downloadCsv(r.getResultId(), createTempFile("fit")).get().getResult();
		assertTrue(f.exists() && f.length() > 0);
		
		MappedResult<UserWorkoutResult> zones = api.timeInZones(ActivityResultType.files, r.getResultId(), IntensityZoneType.pwr).get().getResult();
		assertEquals(1, zones.getResults().size());
		
		// Get the power curve for this activity
		MappedResult<UserWorkoutResult> powercurve = api.powercurve(ActivityResultType.files, r.getResultId(), RelativePeriod.bestever).get().getResult();
		assertNotNull(powercurve.getResults().get(0).getPeak3secWatts());
		
		
		// Do a search on this specific file - query for all the channels which are in the file
		SearchInput<UserWorkoutFileSearch> search = new SearchInput<>(new UserWorkoutFileSearch());
		search.setFields(Arrays.asList("name", "distance", "ascent", "peak3minWatts", "peak20minWatts", "channels"));
		search.setCriteria(new UserWorkoutFileSearch());		
		search.getCriteria().setActivities(Arrays.asList(new VActivity(r.getResultId(), ActivityResultType.files)));		
		MappedSearchResult<UserWorkoutResult> results = api.search(search, 0, 1).get().getResult();
		assertEquals(1, results.getResult().getResults().size());
		
		// Delete it
		assertTrue(api.delete(ActivityResultType.files, r.getResultId()).get().getResult());
		
	} 
	
	@Test
	public void testUploadWith_SRAM_AXS() throws Exception {
		// a completed ride file
		File fit = new File("../z5-sdk-java-core/test-resources/2019-10-31-030841-ELEMNT-ROAM-513E-29-0.fit");
		assertTrue(fit.exists());
		
		// Upload the file
		DataFileUploadIndex r = api.upload(fit, null).get().getResult();
		assertNotNull(r.getId()); // file processing index id
		
		if (r.getState() == FileUploadState.finished || r.getState() == FileUploadState.error) {
			assertTrue(api.delete(ActivityResultType.files, r.getResultId()).get().getResult());
			return;
		}
		
		assertTrue(r.getState() == FileUploadState.pending || r.getState() == FileUploadState.queued);
		
		// Wait for it to process
		while(r.getState() != FileUploadState.finished) {
			Thread.sleep(1000L);
			r = api.uploadStatus(r.getId()).get().getResult();
		}
		
		SearchInput<UserWorkoutFileSearch> search = new SearchInput<>(new UserWorkoutFileSearch());
		search.setFields(Arrays.asList("name", "gears.avgBattery", "gears.batteryStatus", "gears.source", "gears.name", "gears.antId"));
				
		search.setCriteria(new UserWorkoutFileSearch());		
		search.getCriteria().setActivities(Arrays.asList(new VActivity(r.getResultId(), ActivityResultType.files)));		
		MappedSearchResult<UserWorkoutResult> results = api.search(search, 0, 1).get().getResult();
		assertNotNull(results);
				
		// Delete it
		assertTrue(api.delete(ActivityResultType.files, r.getResultId()).get().getResult());
	}
	
	@Test
	public void testUploadWithMetadata() throws Exception {
		// a completed ride file
		File fit = new File("../z5-sdk-java-core/test-resources/2013-12-22-10-30-12.fit");
		assertTrue(fit.exists());
		
		// Set an alternate name and equipment type
		DataFileUploadContext c = new DataFileUploadContext();
		c.setEquipment(Equipment.gravel);
		c.setName("Epic ride");
		c.setBikeId("d584c5cb-e81f-4fbe-bc0d-667e9bcd2c4c");

		// Upload the file
		DataFileUploadIndex r = api.upload(fit, c).get().getResult();
		assertNotNull(r.getId()); // file processing index id
		
		if (r.getState() == FileUploadState.finished) {
			assertTrue(api.delete(ActivityResultType.files, r.getResultId()).get().getResult());
			return;
		}
		
		assertTrue(r.getState() == FileUploadState.pending || r.getState() == FileUploadState.queued);
		
		// Wait for it to process
		while(r.getState() != FileUploadState.finished) {
			Thread.sleep(1000L);
			r = api.uploadStatus(r.getId()).get().getResult();
		}
		
		SearchInput<UserWorkoutFileSearch> search = new SearchInput<>(new UserWorkoutFileSearch());
		search.setFields(Arrays.asList("name", "equipment", "bike.avatar", "bike.serial", "bike.name", "bike.uuid"));
		search.setCriteria(new UserWorkoutFileSearch());		
		search.getCriteria().setActivities(Arrays.asList(new VActivity(r.getResultId(), ActivityResultType.files)));		
		MappedSearchResult<UserWorkoutResult> results = api.search(search, 0, 1).get().getResult();
		assertTrue(results.getResult().getResults().get(0).getName().equals("Epic ride"));
		assertTrue(results.getResult().getResults().get(0).getEquipment().equals(Equipment.gravel));
				
		// Delete it
		assertTrue(api.delete(ActivityResultType.files, r.getResultId()).get().getResult());
	}
	
	@Test
	public void testSpecializedSetRemBikeAssociation() throws Exception {
		if (TEST_BIKE_UUID != null) {
			SearchInput<UserWorkoutFileSearch> search = new SearchInput<>(new UserWorkoutFileSearch());
			search.getCriteria().setIsNotNull(Arrays.asList("fileId"));
			search.setFields(Arrays.asList("bike.avatar", "bike.serial", "bike.name", "bike.uuid"));
			MappedSearchResult<UserWorkoutResult> results = api.search(search, 0, 1).get().getResult();
			assertTrue(results.getCnt() > 0);
			UserWorkoutResult r = results.getResult().getResults().get(0);
			assertNull(r.getBike());
			
			assertTrue(api.setBikeId(r.getActivity(), r.getActivityId(), TEST_BIKE_UUID, null).get().getResult());
			search.getCriteria().setActivities(Arrays.asList(new VActivity(r.getActivityId(), r.getActivity())));
			results = api.search(search, 0, 1).get().getResult();
			r = results.getResult().getResults().get(0);
			assertNotNull(r.getBike());
			assertNotNull(r.getBike().getSerial());
			assertNotNull(r.getBike().getAvatar());
			assertNotNull(r.getBike().getName());
			
			assertTrue(api.removeBikeId(r.getActivity(), r.getActivityId(), null).get().getResult());
			results = api.search(search, 0, 1).get().getResult();
			r = results.getResult().getResults().get(0);
			assertNull(r.getBike());
		}
	}
	
	
	@Test
	public void testSearchForLast10Activities() throws Exception {
		SearchInput<UserWorkoutFileSearch> search = new SearchInput<>(new UserWorkoutFileSearch());
		
		// Just request some summary fields
		search.setFields(Arrays.asList("name", "distance", "training", "avgWatts", "avgBpm", "lat1", "lon1", "startTs", "locality", "peak3secWatts", "headunit.name", "aap.avgWatts"));
		
		// We only want completed rides with files
		search.getCriteria().setIsNotNull(Arrays.asList("fileId"));
		
		// Order by ride start time desc
		search.getCriteria().setOrder(Arrays.asList(new Order("startTs", com.zone5cloud.core.enums.Order.desc)));
		
		// Limit to rides with a startTs <= now - ie avoid dodgy files which might have a timestamp in the future!
		search.getCriteria().setToTs(System.currentTimeMillis());
		
		MappedSearchResult<UserWorkoutResult> results = api.search(search, 0, 10).get().getResult();
		
		assertTrue(results.getResult().getResults().size() <= 10);
		for(int i=1;i<results.getResult().getResults().size();i++)
			assertTrue(results.getResult().getResults().get(i).getStartTs() <= results.getResult().getResults().get(i-1).getStartTs());
		
		// Get the next batch of 10
		if (results.getCnt() > 10) {
			results = api.next(10, 10).get().getResult();
			if (results.getResult() != null) {
				assertTrue(results.getResult().getResults().size() <= 10);
				for(int i=1;i<results.getResult().getResults().size();i++)
					assertTrue(results.getResult().getResults().get(i).getStartTs() <= results.getResult().getResults().get(i-1).getStartTs());
			}
		}
	}
	
	@Test
	public void testSearchForRidesOfSpecificDistance() throws Exception {
		SearchInput<UserWorkoutFileSearch> search = new SearchInput<>(new UserWorkoutFileSearch());
		
		// Just request some summary fields - including power meter manufacturer, battery level and eBike power info
		search.setFields(Arrays.asList("name", "distance", "training", "pwrManufacturer", "pwrBattery", "turbo.avgMotorPower"));
		
		// We only want completed rides with files
		search.getCriteria().setIsNotNull(Arrays.asList("fileId"));
		search.getCriteria().setRanges(new HashMap<String, List<Double>>());
		search.getCriteria().getRanges().put("distance", Arrays.asList(20000d, 30000d)); // 20-30km
		
		// We only want rides
		search.getCriteria().setSports(Arrays.asList(ActivityType.ride));
		
		// Order by ride start time desc
		search.getCriteria().setOrder(Arrays.asList(new Order("startTs", com.zone5cloud.core.enums.Order.desc)));
		
		// Limit to rides which were done in the last 12 months
		search.getCriteria().setFromTs(System.currentTimeMillis() - (1000L*60*60*24*365));
		
		MappedSearchResult<UserWorkoutResult> results = api.search(search, 0, 10).get().getResult();
		
		assertTrue(results.getResult().getResults().size() <= 10);
		for(UserWorkoutResult r : results.getResult().getResults())
			assertTrue(r.getDistance() >= 20000 && r.getDistance() <= 30000);	
	}
	
	@Test
	public void testSearchForRidesInSpecificDateRanges() throws Exception {
		SearchInput<UserWorkoutFileSearch> search = new SearchInput<>(new UserWorkoutFileSearch());
		
		// Just request some summary fields - including power meter manufacturer, battery level and eBike power info
		search.setFields(Arrays.asList("name", "distance", "training"));
		
		// We only want completed rides with files
		search.getCriteria().setIsNotNull(Arrays.asList("fileId"));
		
		search.getCriteria().setRangesTs(new ArrayList<>());
		
		Calendar c = new GregorianCalendar(TimeZone.getTimeZone("Australia/Sydney"));
		c.set(Calendar.YEAR, 2018);
		c.set(Calendar.MONTH, Calendar.JANUARY);
		c.set(Calendar.DAY_OF_YEAR, 1);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		
		// First 30 days of 2018
		search.getCriteria().getRangesTs().add(new DateRange(c.getTimeInMillis(), c.getTimeInMillis()+(1000L*60*60*24*30), "Australia/Sydney"));
		
		c.add(Calendar.YEAR, 1);
		// First 30 days of 2019
		search.getCriteria().getRangesTs().add(new DateRange(c.getTimeInMillis(), c.getTimeInMillis()+(1000L*60*60*24*30), "Australia/Sydney"));
		
		MappedSearchResult<UserWorkoutResult> results = api.search(search, 0, 10).get().getResult();
		assertNotNull(results);
	}
	
	@Test
	public void testSearchForIncompleteWorkouts() throws Exception {
		
		SearchInput<UserWorkoutFileSearch> search = new SearchInput<>(new UserWorkoutFileSearch());
		
		search.setFields(Arrays.asList("scheduled.name", "scheduled.day", "scheduled.tz", "workout"));
		
		search.getCriteria().setToTs(System.currentTimeMillis());
		
		search.getCriteria().setIsNull(Arrays.asList("fileId"));
		search.getCriteria().setState(UserWorkoutState.pending);
		search.getCriteria().setExcludeWorkouts(Arrays.asList(WorkoutType.rest));
		
		search.getCriteria().setOrder(Arrays.asList(new Order("scheduled.day", com.zone5cloud.core.enums.Order.desc)));
		
		MappedSearchResult<UserWorkoutResult> results = api.search(search, 0, 10).get().getResult();
		assertNotNull(results);
	}
	
	@Test
	public void testSearchForUpcomingWorkouts() throws Exception {
		SearchInput<UserWorkoutFileSearch> search = new SearchInput<>(new UserWorkoutFileSearch());
		
		search.setFields(Arrays.asList("scheduled.day", "scheduled.tz", "scheduled.name", "scheduled.tscorepwr", "scheduled.durationSecs", "scheduled.distance", "scheduled.workout", "scheduled.preDescr"));
		search.getCriteria().setOrder(Arrays.asList(new Order("scheduled.day", com.zone5cloud.core.enums.Order.asc)));
		search.getCriteria().setFromTs(System.currentTimeMillis());
		
		MappedSearchResult<UserWorkoutResult> results = api.search(search, 0, 10).get().getResult();
		assertNotNull(results);
	}
	
	@Test
	public void testComplexSearch() throws Exception {
		SearchInput<UserWorkoutFileSearch> search = new SearchInput<>(new UserWorkoutFileSearch());
		
		search.setFields(Arrays.asList("name", "distance", "ascent", "peak3minWatts", "peak20minWatts"));
		search.setCriteria(new UserWorkoutFileSearch());
		search.getCriteria().setRanges(new HashMap<>());
		
		// Distance 20-50km
		search.getCriteria().getRanges().put("distance", Arrays.asList(20000d,50000d));
		
		// Ascent 10-1000m
		search.getCriteria().getRanges().put("ascent", Arrays.asList(10d,1000d));
		
		// At last 30mins in duration
		search.getCriteria().getRanges().put("training", Arrays.asList(30*60d));
		
		// Has cadence and heart rate
		search.getCriteria().setIsNotNull(Arrays.asList("avgCadence", "avgBpm"));
		
		// Only road rides
		search.getCriteria().setEquipment(Equipment.road);
		
		// Only rides
		search.getCriteria().setSports(Arrays.asList(ActivityType.ride));
		
		// Contains this name
		search.getCriteria().setName("Foo");
		
		
		MappedSearchResult<UserWorkoutResult> results = api.search(search, 0, 10).get().getResult();
		assertNotNull(results);
	}

}
