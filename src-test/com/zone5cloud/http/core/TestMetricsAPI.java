package com.zone5cloud.http.core;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import com.zone5cloud.core.activities.UserWorkoutResultAggregates;
import com.zone5cloud.core.search.MappedResult;
import com.zone5cloud.core.utils.GsonManager;
import com.zone5cloud.http.core.api.MetricsAPI;

public class TestMetricsAPI extends BaseTest {

	MetricsAPI api = new MetricsAPI();
	
	@Before
	public void setup() throws InterruptedException, ExecutionException {
		login();
	}
	
	@Test
	public void testQuery() throws Exception {
		if (TEST_BIKE_UUID != null && !TEST_BIKE_UUID.isEmpty()) {
			MappedResult<UserWorkoutResultAggregates> result = api.getBikeMetrics(null, Arrays.asList("sum.training","sum.distance","sum.ascent","wavg.avgSpeed","max.maxSpeed","wavg.avgWatts","max.maxWatts"), Arrays.asList(TEST_BIKE_UUID)).get().getResult();
			assertNotNull(result.getResults());
			// Assuming you have a match for this bike id, you would have result which looks like;
			/* {
	  "results": [
	    {
	      "sum": {
	        "training": 3148,
	        "distance": 25222.949219,
	        "ascent": 132
	      },
	      "max": {
	        "maxSpeed": 54.327599,
	        "maxWatts": 689
	      },
	      "wavg": {
	        "avgSpeed": 28.8432,
	        "avgWatts": 146
	      },
	      "count": 1,
	      "user": {
	        "id": 199,
	        "firstname": "Andrew",
	        "lastname": "Hall"
	      },
	      "name": "Series",
	      "bike": {
	        "uuid": "d584c5cb-e81f-4fbe-bc0d-667e9bcd2c4c"
	      }
	    }
	  ]
	} */
			System.out.println(GsonManager.getInstance(true).toJson(result));
		}
	} 
	
}
