package com.zone5cloud.http.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import com.zone5cloud.core.enums.Equipment;
import com.zone5cloud.core.ride.DateTime;
import com.zone5cloud.core.ride.Region;
import com.zone5cloud.core.ride.ScheduledActivityEffort;
import com.zone5cloud.core.ride.ScheduledActivityMaskOpts;
import com.zone5cloud.core.ride.UserScheduledActivity;
import com.zone5cloud.core.ride.UserScheduledActivitySearch;
import com.zone5cloud.core.ride.UserScheduledActivityType;
import com.zone5cloud.core.search.DayRange;
import com.zone5cloud.core.search.Order;
import com.zone5cloud.core.search.SearchResult;
import com.zone5cloud.core.users.User;
import com.zone5cloud.http.core.api.RideAPI;

public class TestUserScheduledActivities extends BaseTest {

	private final RideAPI rides = new RideAPI();
	
	@Before
	public void setup() throws InterruptedException, ExecutionException {
		login();
	}
	
	/**
	 * Note - I don't appear to have permission to add a new ride...?
	 */
	@Test
	public void testCreateNewRide() throws Exception {
		
		User me = me();
		System.out.println(toJson(me));
			
		UserScheduledActivity ride = new UserScheduledActivity();
		
		ride.setName("Test the best - Mt Stromlo");
		ride.setDescription("Come and test the best bikes this weekend at Mt Stromlo!");
		ride.setAtype(UserScheduledActivityType.demo);
		
		// max participants - 0 for unlimited
		ride.setMax(0);
		
		// meeting point
		ride.setMeeting("Stromlo Forest Park");
		ride.setAddress("Opperman Ave, Stromlo ACT 2611");
		ride.setLat(-35.31392);
		ride.setLon(149.02710);
		
		// ride distance in meters
		ride.setDistance(20000d);
		
		// ride distance in meters - only needs to be set if a distance range needs to be set
		ride.setDistanceHi(50000d);
		
		// lets make it a turbo friendly ride!
		ride.setOpts(new HashSet<>(1));
		ride.getOpts().add(ScheduledActivityMaskOpts.turbo);
		
		ride.setEfforts(new HashSet<>(2));
		ride.getEfforts().add(ScheduledActivityEffort.easy);
		ride.getEfforts().add(ScheduledActivityEffort.moderate);
		
		ride.setTypes(new HashSet<>(3));
		ride.getTypes().add(Equipment.road);
		ride.getTypes().add(Equipment.mtb);
		ride.getTypes().add(Equipment.gravel);
		
		ride.setEventUrl("https://specialized.com");
	
		// set the start date/time - 9am!
		ride.setTime(new DateTime());
		ride.getTime().setYear(2019);
		ride.getTime().setDay(360);
		ride.getTime().setTime(900);
		
		// only needed for a date range - finishes 4:30pm
		ride.setTimeEnd(new DateTime());
		ride.getTimeEnd().setYear(2019);
		ride.getTimeEnd().setDay(364);
		ride.getTimeEnd().setTime(1630);
		
		// Insert a new entry
		UserScheduledActivity result = rides.add(ride).get().getResult();
		assertNotNull(result);
		assertNotNull(result.getId());
		System.out.println(toJson(result));
		
		// Get the summary record
		result = rides.getSummary(result.getId()).get().getResult();
		assertNotNull(result);
		assertNotNull(result.getId());
		System.out.println(toJson(result));
		
		// Get the detailed record
		result = rides.getDetailed(result.getId()).get().getResult();
		assertNotNull(result);
		assertNotNull(result.getId());
		System.out.println(toJson(result));
		
		// Search
		UserScheduledActivitySearch search = new UserScheduledActivitySearch();
		search.setAtypes(Arrays.asList(UserScheduledActivityType.demo, UserScheduledActivityType.event));
		search.setRegions(Arrays.asList(Region.pacific));
		search.setDayRange(new DayRange());
		search.getDayRange().setFromYear(2019);
		search.getDayRange().setFromDay(200);
		search.getDayRange().setToYear(2019);
		search.getDayRange().setToDay(365);
		search.setOrder(Arrays.asList(new Order("ts", com.zone5cloud.core.enums.Order.asc)));
		
		// get first 10 results
		SearchResult<UserScheduledActivity> results = rides.search(search, 0, 10).get().getResult();
		System.out.println(toJson(results));
		assertTrue(results.getCnt() > 0);
		assertTrue(results.getResults().size() > 0);
		
		// get the next 10 results
		if (results.getCnt() > 10)
			results = rides.next(10, 10).get().getResult();
		
		// delete the added ride
		assertTrue(rides.delete(result.getId()).get().getResult());
	}
}
