package com.zone5cloud.http.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import com.zone5cloud.core.enums.UserConnectionsType;
import com.zone5cloud.core.thirdpartyconnections.PushRegistration;
import com.zone5cloud.core.thirdpartyconnections.PushRegistrationResponse;
import com.zone5cloud.core.thirdpartyconnections.ThirdPartyToken;
import com.zone5cloud.core.thirdpartyconnections.ThirdPartyTokenResponse;
import com.zone5cloud.http.core.api.ThirdPartyConnectionsAPI;
import com.zone5cloud.http.core.responses.Z5HttpResponse;

public class TestThirdPartyConnections extends BaseTest {

	ThirdPartyConnectionsAPI api = new ThirdPartyConnectionsAPI();
	
	@Before
	public void setup() throws InterruptedException, ExecutionException {
		login();
	}
	
	@Test
	public void testThirdPartyTokenCrud() throws Exception {
		
		ThirdPartyTokenResponse rsp = api.hasThirdPartyToken(UserConnectionsType.strava).get().getResult();
		assertFalse(rsp.getAvailable());
		
		rsp = api.setThirdPartyToken(UserConnectionsType.strava, new ThirdPartyToken("abc123", "refreshme", "notmuch", 3600)).get().getResult();
		assertTrue(rsp.getSuccess());
		
		rsp = api.hasThirdPartyToken(UserConnectionsType.strava).get().getResult();
		assertTrue(rsp.getAvailable());
		assertEquals("abc123", rsp.getToken().getToken());
		assertEquals("refreshme", rsp.getToken().getRefreshToken());
		assertEquals("notmuch", rsp.getToken().getScope());
		assertNotNull(rsp.getToken().getExpiresIn());
		
		rsp = api.removeThirdPartyToken(UserConnectionsType.strava).get().getResult();
		assertTrue(rsp.getSuccess());
		
		rsp = api.hasThirdPartyToken(UserConnectionsType.strava).get().getResult();
		assertFalse(rsp.getAvailable());
	}
	
	@Test
	public void testRegisterDevice() throws Exception {
		Z5HttpResponse<PushRegistrationResponse> response = api.registerDeviceWithThirdParty(new PushRegistration("abc123", "android", "my-device123")).get();
		assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 300);
		assertNull(response.getError());
		
		PushRegistrationResponse rsp = response.getResult();
		assertNotNull(rsp.getToken());
		Long token = rsp.getToken(); // save to compare
		
		// repeat to show that response gives SAME token
		rsp = api.registerDeviceWithThirdParty(new PushRegistration("abc123", "android", "my-device123")).get().getResult();
		assertEquals(token, rsp.getToken());
		
		// now delete it
		Z5HttpResponse<Void> response2 = api.deregisterDeviceWithThirdParty("abc123").get();
		assertTrue(response2.getStatusCode() >= 200 && response2.getStatusCode() < 300);
		assertNull(response2.getError());
		
		// now create a new one and check that it is DIFFERENT (because the first one was successfully deleted)
		rsp = api.registerDeviceWithThirdParty(new PushRegistration("abc123", "android", "my-device123")).get().getResult();
		assertNotEquals(token, rsp.getToken());
		
		// cleanup
		api.deregisterDeviceWithThirdParty("abc123").get();
		assertTrue(response2.getStatusCode() >= 200 && response2.getStatusCode() < 300);
		assertNull(response2.getError());
	}

}
