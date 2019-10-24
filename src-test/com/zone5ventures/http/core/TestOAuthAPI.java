package com.zone5ventures.http.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.zone5ventures.core.oauth.OAuthToken;
import com.zone5ventures.core.users.User;
import com.zone5ventures.http.core.api.OAuthAPI;
import com.zone5ventures.http.core.api.UserAPI;

public class TestOAuthAPI extends BaseTest {

	OAuthAPI api = new OAuthAPI();
	
	@Test
	public void testGetUserToken() throws Exception {
		
		Z5HttpClient.get().setToken(null);
		Z5HttpClient.get().setHostname("127.0.0.1:8080");
		
		String clientId = "<your OAuth clientId issued by Zone5>";
		String secret   = "<your OAuth secret issued by Zone5>";
		String redirectURI = "https://localhost"; // OR WHAT EVER REDIRECT YOU HAVE REGISTERED WITH US
		
		String username = "<email address of the user you want a token for>";
		String password = "<password of the user you want a token for>";
		
		OAuthToken response = api.newAccessToken(clientId, secret, redirectURI, username, password).get().getResult();
		assertNotNull(response.getAccess_token());
		
		Z5HttpClient.get().setToken(response.getAccess_token());
		
		User me = new UserAPI().me().get().getResult();
		assertEquals(me.getEmail(), username);
	}

}
