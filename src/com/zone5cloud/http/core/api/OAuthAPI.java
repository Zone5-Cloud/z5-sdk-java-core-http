package com.zone5cloud.http.core.api;

import java.util.concurrent.Future;

import com.zone5cloud.core.Types;
import com.zone5cloud.core.Z5Error;
import com.zone5cloud.core.enums.GrantType;
import com.zone5cloud.core.oauth.OAuthToken;
import com.zone5cloud.core.oauth.OAuthTokenRequest;
import com.zone5cloud.core.users.Users;
import com.zone5cloud.http.core.AbstractAPI;
import com.zone5cloud.http.core.Z5HttpClient;
import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseHandler;

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
		OAuthTokenRequest request = new OAuthTokenRequest();
		request.setUsername(username);
		request.setPassword(password);
		request.setClientId(clientId);
		request.setClientSecret(secret);
		request.setRedirect(redirect);
		request.setGrantType(GrantType.USERNAME_PASSWORD);
		
		final Z5HttpClient client = getClient();
		
		return getClient().doFormPost(Types.OAUTHTOKEN, Users.NEW_ACCESS_TOKEN, request, new Z5HttpResponseHandler<OAuthToken>() {

			@Override
			public void onSuccess(int code, OAuthToken result) {
				if (result.getExpiresIn() != null) {
					result.setTokenExp(System.currentTimeMillis() + (result.getExpiresIn() * 1000));
				}
				client.setToken(result);
				if (handler != null) {
					handler.onSuccess(code, result);
				}
			}

			@Override
			public void onError(int code, Z5Error error) {
				if (handler != null) {
					handler.onError(code, error);
				}
			}

			@Override
			public void onError(Throwable t, Z5Error error) {
				if (handler != null) {
					handler.onError(t, error);
				}
			}
		});
	}
	
	/**
	 * Refresh auth token with refresh token
	 */
	public Future<Z5HttpResponse<OAuthToken>> refreshAccessToken(String clientId, String secret, String username, String refreshToken) {
		return refreshAccessToken(clientId, secret, username, refreshToken, null);
	}
	
	/**
	 * Refresh auth token with refresh token
	 */
	public Future<Z5HttpResponse<OAuthToken>> refreshAccessToken(String clientId, String secret, String username, String refreshToken, Z5HttpResponseHandler<OAuthToken> handler) {
		OAuthTokenRequest request = new OAuthTokenRequest();
		request.setUsername(username);
		request.setRefreshToken(refreshToken);
		request.setClientId(clientId);
		request.setClientSecret(secret);
		request.setGrantType(GrantType.REFRESH_TOKEN);
		
		final Z5HttpClient client = getClient();
		return client.doFormPost(Types.OAUTHTOKEN, Users.NEW_ACCESS_TOKEN, request, new Z5HttpResponseHandler<OAuthToken>() {

			@Override
			public void onSuccess(int code, OAuthToken result) {
				if (result.getExpiresIn() != null) {
					result.setTokenExp(System.currentTimeMillis() + (result.getExpiresIn() * 1000));
				}
				client.setToken(result);
				if (handler != null) {
					handler.onSuccess(code, result);
				}
			}

			@Override
			public void onError(int code, Z5Error error) {
				if (handler != null) {
					handler.onError(code, error);
				}
			}

			@Override
			public void onError(Throwable t, Z5Error error) {
				if (handler != null) {
					handler.onError(t, error);
				}
			}
		});
	}
	
	/**
	 * Refresh auth token with refresh token
	 */
	public Future<Z5HttpResponse<OAuthToken>> adhocAccessToken(String clientId) {
		return adhocAccessToken(clientId, null);
	}
	
	/**
	 * Refresh auth token with refresh token
	 */
	public Future<Z5HttpResponse<OAuthToken>> adhocAccessToken(String clientID, Z5HttpResponseHandler<OAuthToken> handler) {
		return getClient().doGet(Types.OAUTHTOKEN, Users.NEW_ADHOC_ACCESS_TOKEN.replace("{clientId}", clientID), new Z5HttpResponseHandler<OAuthToken>() {

			@Override
			public void onSuccess(int code, OAuthToken result) {
				if (result.getExpiresIn() != null) {
					result.setTokenExp(System.currentTimeMillis() + (result.getExpiresIn() * 1000));
				}
				
				if (handler != null) {
					handler.onSuccess(code, result);
				}
			}

			@Override
			public void onError(int code, Z5Error error) {
				if (handler != null) {
					handler.onError(code, error);
				}
			}

			@Override
			public void onError(Throwable t, Z5Error error) {
				if (handler != null) {
					handler.onError(t, error);
				}
			}
		});
	}
}
