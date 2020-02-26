package com.zone5ventures.http.core.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import com.zone5ventures.core.Types;
import com.zone5ventures.core.oauth.OAuthTokenAlt;
import com.zone5ventures.core.users.LoginResponse;
import com.zone5ventures.core.users.RegisterUser;
import com.zone5ventures.core.users.User;
import com.zone5ventures.core.users.Users;
import com.zone5ventures.http.core.AbstractAPI;
import com.zone5ventures.http.core.responses.Z5HttpResponse;
import com.zone5ventures.http.core.responses.Z5HttpResponseHandler;

public class UserAPI extends AbstractAPI {
		
	public Future<Z5HttpResponse<User>> me() {
		return me(null);
	}
	
	public Future<Z5HttpResponse<User>> me(Z5HttpResponseHandler<User> handler) {
		return getClient().doGet(Types.USER, Users.ME, handler);
	}
	
	/** Register a new user account */
	public Future<Z5HttpResponse<User>> register(RegisterUser user) {
		return getClient().doPost(Types.USER, Users.REGISTER_USER, user, null);
	}
	
	/** Register a new user account */
	public Future<Z5HttpResponse<User>> register(RegisterUser user, Z5HttpResponseHandler<User> handler) {
		return getClient().doPost(Types.USER, Users.REGISTER_USER, user, handler);
	}
	
	/** Delete a user account */
	public Future<Z5HttpResponse<Void>> deleteAccount(long userId) {
		return getClient().doGet(Types.VOID, Users.DELETE_USER.replace("{userId}", ""+userId), null);
	}
	
	/** Delete a user account */
	public Future<Z5HttpResponse<Void>> deleteAccount(long userId, Z5HttpResponseHandler<Void> handler) {
		return getClient().doGet(Types.VOID, Users.DELETE_USER.replace("{userId}", ""+userId), handler);
	}

	/** Login as a user and obtain a bearer token - clientId and clientSecret are not required in Specialized featureset */
	public Future<Z5HttpResponse<LoginResponse>> login(String email, String password, String clientId, String clientSecret) {
		return login(email, password, clientId, clientSecret, null);
	}		
	
	/** Login as a user and obtain a bearer token - clientId and clientSecret are not required in Specialized featureset */
	public Future<Z5HttpResponse<LoginResponse>> login(String email, String password, String clientId, String clientSecret, Z5HttpResponseHandler<LoginResponse> handler) {
		Map<String, String> m = new HashMap<>(5);
		m.put("username", email);
		m.put("password", password);
		m.put("token", Boolean.TRUE.toString());
		if (clientId != null && clientSecret != null) {
			m.put("clientId", clientId);
			m.put("clientSecret", clientSecret);
		} else if (!getClient().isSpecialized()) {
			throw new IllegalArgumentException("clientId and clientSecret are required");
		}
		return getClient().doPost(Types.LOGIN_RESPONSE, Users.LOGIN, m, handler);		
	}
	
	/** Logout - this will invalidate any active JSESSION and will also invalidate your bearer token */
	public Future<Z5HttpResponse<Boolean>> logout() {
		return logout(null);
	}
	
	/** Logout - this will invalidate any active JSESSION and will also invalidate your bearer token */
	public Future<Z5HttpResponse<Boolean>> logout(Z5HttpResponseHandler<Boolean> handler) {
		return getClient().doGet(Types.BOOLEAN, Users.LOGOUT, handler);
	}
	
	/** Test if an email address is already registered in the system - true if the email already exists in the system */
	public Future<Z5HttpResponse<Boolean>> isEmailRegistered(String email) {
		return isEmailRegistered(email, null);
	}
	
	/** Test if an email address is already registered in the system - true if the email already exists in the system */
	public Future<Z5HttpResponse<Boolean>> isEmailRegistered(String email, Z5HttpResponseHandler<Boolean> handler) {
		return getClient().doPost(Types.BOOLEAN, Users.EMAIL_EXISTS, email, handler);
	}
	
	/** Request a password reset email - ie get a magic link to reset a user's password */
	public Future<Z5HttpResponse<Boolean>> resetPassword(String email) {
		return resetPassword(email, null);
	}
	
	/** Request a password reset email - ie get a magic link to reset a user's password */
	public Future<Z5HttpResponse<Boolean>> resetPassword(String email, Z5HttpResponseHandler<Boolean> handler) {
		return getClient().doPost(Types.BOOLEAN, Users.PASSWORD_RESET, email, handler);
	}
	
	/** Change a user's password - oldPassword is only required in Specialized environment */
	public Future<Z5HttpResponse<Void>> changePassword(String oldPassword, String newPassword) {
		return changePassword(oldPassword, newPassword, null);
	}
	
	/** Change a user's password - oldPassword is only required in Specialized environment */
	public Future<Z5HttpResponse<Void>> changePassword(String oldPassword, String newPassword, Z5HttpResponseHandler<Void> handler) {
		if (getClient().isSpecialized()) {
			Map<String, String> m = new HashMap<>(2);
			m.put("oldPassword", oldPassword);
			m.put("newPassword", newPassword);
			return getClient().doPost(Types.VOID, Users.CHANGE_PASSWORD_SPECIALIZED, m, handler);
		} else {
			User u = new User();
			u.setPassword(newPassword);
			return getClient().doPost(Types.VOID, Users.SET_USER, u, handler);
		}
		
	}
	
	/** Refresh a bearer token - get a new token if the current one is nearing expiry */
	public Future<Z5HttpResponse<OAuthTokenAlt>> refreshToken() {
		return refreshToken(null);
	}
	
	/** Refresh a bearer token - get a new token if the current one is nearing expiry */
	public Future<Z5HttpResponse<OAuthTokenAlt>> refreshToken(Z5HttpResponseHandler<OAuthTokenAlt> handler) {
		return getClient().doGet(Types.OAUTHTOKENALT, Users.REFRESH_TOKEN, handler);
	}
}
