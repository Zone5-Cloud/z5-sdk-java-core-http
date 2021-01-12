package com.zone5cloud.http.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import com.zone5cloud.core.thirdpartyconnections.UpgradeAvailableResponse;
import com.zone5cloud.http.core.api.UserAgentAPI;
import com.zone5cloud.http.core.responses.Z5HttpResponse;

public class TestUserAgent extends BaseTest {

	private UserAgentAPI api = new UserAgentAPI();
	
	@Before
	public void setup() throws InterruptedException, ExecutionException {
		login();
	}
	
	@Test
	public void testDeprecated() throws Exception {
		Z5HttpResponse<UpgradeAvailableResponse> response = api.getDeprecated().get();
		assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 300);
		assertNull(response.getError());
		assertNotNull(response.getResult());
		
		assertFalse(response.getResult().getIsUpgradeAvailable());
		
		// Now do it again but set the client agent such that getDeprecated will return true
		Z5HttpClient.get().setUserAgent("ride-iOS/1.2.3 (10)");
		
		response = api.getDeprecated().get();
		assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 300);
		assertNull(response.getError());
		assertNotNull(response.getResult());
		
		assertTrue(response.getResult().getIsUpgradeAvailable());
		
	}
}
