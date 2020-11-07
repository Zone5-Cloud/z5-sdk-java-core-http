package com.zone5cloud.http.core.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import com.zone5cloud.http.core.AbstractAPI;
import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseHandler;
import com.zone5cloud.core.Types;
import com.zone5cloud.core.oauth.OAuthToken;
import com.zone5cloud.core.users.Users;

public class OAuthAPI extends AbstractAPI {
		
	/**
	 * Get new auth token with password
	 */
	public Future<Z5HttpResponse<OAuthToken>> newAccessToken(String clientId, String secret, String redirect, String username, String password) {
		return newAccessToken(clientId, secret, redirect, username, password, null);
	}
	
	/**
	 * Get new auth token with password
	 */
	public Future<Z5HttpResponse<OAuthToken>> newAccessToken(String clientId, String secret, String redirect, String username, String password, Z5HttpResponseHandler<OAuthToken> handler) {
		Map<String, String> entity = new HashMap<>();
		entity.put("username", username);
		entity.put("password", password);
		entity.put("client_id", clientId);
		entity.put("client_secret", secret);
		entity.put("grant_type", "password");
		entity.put("redrect_uri", redirect);
		
		return getClient().doFormPost(Types.OAUTHTOKEN, Users.NEW_ACCESS_TOKEN, entity, handler);
	}
	
	/**
	 * Refresh auth token with refresh token
	 */
	public Future<Z5HttpResponse<OAuthToken>> refreshAccessToken(String clientId, String secret, String redirect, String username, String refreshToken) {
		return newAccessToken(clientId, secret, redirect, username, refreshToken, null);
	}
	
	/**
	 * Refresh auth token with refresh token
	 */
	public Future<Z5HttpResponse<OAuthToken>> refreshAccessToken(String clientId, String secret, String redirect, String username, String refreshToken, Z5HttpResponseHandler<OAuthToken> handler) {
		Map<String, String> entity = new HashMap<>();
		entity.put("username", username);
		entity.put("refresh_token", refreshToken);
		entity.put("client_id", clientId);
		entity.put("client_secret", secret);
		entity.put("grant_type", "refresh_token");
		entity.put("redrect_uri", redirect);
		
		return getClient().doFormPost(Types.OAUTHTOKEN, Users.NEW_ACCESS_TOKEN, entity, handler);
	}
}
