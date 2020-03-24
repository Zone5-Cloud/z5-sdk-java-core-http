package com.zone5cloud.http.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.zone5cloud.http.core.api.ThirdPartyConnectionsAPI;
import com.zone5cloud.core.enums.UserConnectionsType;
import com.zone5cloud.core.thirdpartyconnections.ThirdPartyToken;
import com.zone5cloud.core.thirdpartyconnections.ThirdPartyTokenResponse;

public class TestThirdPartyConnections extends BaseTest {

	ThirdPartyConnectionsAPI api = new ThirdPartyConnectionsAPI();
	
	@Test
	public void testThirdPartyTokenCrud() throws Exception {
		
		ThirdPartyTokenResponse rsp = api.has_third_party_token(UserConnectionsType.strava).get().getResult();
		assertFalse(rsp.getAvailable());
		
		rsp = api.set_third_party_token(UserConnectionsType.strava, new ThirdPartyToken("abc123", "refreshme", "notmuch", 3600)).get().getResult();
		assertTrue(rsp.getSuccess());
		
		rsp = api.has_third_party_token(UserConnectionsType.strava).get().getResult();
		assertTrue(rsp.getAvailable());
		assertEquals("abc123", rsp.getToken().getToken());
		assertEquals("refreshme", rsp.getToken().getRefresh_token());
		assertEquals("notmuch", rsp.getToken().getScope());
		assertNotNull(rsp.getToken().getExpires_in());
		
		rsp = api.remove_third_party_token(UserConnectionsType.strava).get().getResult();
		assertTrue(rsp.getSuccess());
		
		rsp = api.has_third_party_token(UserConnectionsType.strava).get().getResult();
		assertFalse(rsp.getAvailable());
	}

}
