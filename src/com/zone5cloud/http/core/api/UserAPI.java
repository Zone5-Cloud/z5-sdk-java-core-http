package com.zone5cloud.http.core.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import com.zone5cloud.core.Types;
import com.zone5cloud.core.Z5Error;
import com.zone5cloud.core.oauth.OAuthToken;
import com.zone5cloud.core.users.LoginRequest;
import com.zone5cloud.core.users.LoginResponse;
import com.zone5cloud.core.users.NewPassword;
import com.zone5cloud.core.users.RegisterUser;
import com.zone5cloud.core.users.User;
import com.zone5cloud.core.users.UserPreferences;
import com.zone5cloud.core.users.Users;
import com.zone5cloud.http.core.AbstractAPI;
import com.zone5cloud.http.core.Z5HttpClient;
import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseHandler;

public class UserAPI extends AbstractAPI {
		
	public Future<Z5HttpResponse<User>> me() {
		return me(null);
	}
	
	public Future<Z5HttpResponse<User>> me(Z5HttpResponseHandler<User> handler) {
		return getClient().doGet(Types.USER, Users.ME, handler);
	}

	public Future<Z5HttpResponse<UserPreferences>> getUserPreferences(long userId) {
		return getUserPreferences(userId, null);
	}
	
	public Future<Z5HttpResponse<UserPreferences>> getUserPreferences(long userId, Z5HttpResponseHandler<UserPreferences> handler) {
		return getClient().doGet(Types.USER_PREFERENCES, Users.GET_USER_PREFERENCES.replace("{userId}", ""+userId), handler);
	}
	
	public Future<Z5HttpResponse<Boolean>> setUserPreferences(UserPreferences input) {
		return setUserPreferences(input, null);
	}
	
	public Future<Z5HttpResponse<Boolean>> setUserPreferences(UserPreferences input, Z5HttpResponseHandler<Boolean> handler) {
		return getClient().doPost(Types.BOOLEAN, Users.SET_USER_PREFERENCES, input, handler);
	}
	
	/** Register a new user account */
	public Future<Z5HttpResponse<User>> register(RegisterUser user) {
		return register(user, null);
	}
	
	/** Register a new user account */
	public Future<Z5HttpResponse<User>> register(RegisterUser user, Z5HttpResponseHandler<User> handler) {
		return getClient().doPost(Types.USER, Users.REGISTER_USER, user, handler);
	}
	
	/** Delete a user account */
	public Future<Z5HttpResponse<Void>> deleteAccount(long userId) {
		return deleteAccount(userId, null);
	}
	
	/** Delete a user account */
	public Future<Z5HttpResponse<Void>> deleteAccount(long userId, Z5HttpResponseHandler<Void> handler) {
		return getClient().doGet(Types.VOID, Users.DELETE_USER.replace("{userId}", ""+userId), handler);		
	}

	/** Login as a user and obtain a bearer token - clientId and clientSecret are for cognito, not required for Gigya */
	public Future<Z5HttpResponse<LoginResponse>> login(String email, String password, String clientId, String clientSecret) {
		return login(email, password, clientId, clientSecret, null);
	}		
	
	/** Login as a user and obtain a bearer token - clientId and clientSecret are for cognito, not required for Gigya */
	public Future<Z5HttpResponse<LoginResponse>> login(String email, String password, String clientId, String clientSecret, Z5HttpResponseHandler<LoginResponse> handler) {
		LoginRequest request = new LoginRequest();
		request.setUsername(email);
		request.setPassword(password);
		request.setToken(true);
		request.setClientId(clientId);
		request.setClientSecret(clientSecret);
		return login(request, handler);
	}
		
	/** Login as a user and obtain a bearer token - clientId and clientSecret are for cognito, not required for Gigya */
	public Future<Z5HttpResponse<LoginResponse>> login(LoginRequest request) {
		return login(request, null);
	}
	
	/** Login as a user and obtain a bearer token - clientId and clientSecret are for cognito, not required for Gigya */
	public Future<Z5HttpResponse<LoginResponse>> login(LoginRequest request, Z5HttpResponseHandler<LoginResponse> handler) {
		
		final Z5HttpClient client = getClient();
		return client.doPost(Types.LOGIN_RESPONSE, Users.LOGIN, request, new Z5HttpResponseHandler<LoginResponse>() {

			@Override
			public void onSuccess(int code, LoginResponse result) {
				OAuthToken token = new OAuthToken(result);
				client.setToken(token);

				if(result !=null && result.getUser() != null ){
					if(result.getUser().getEmail() != null){
						client.setUserName(result.getUser().getEmail());
					}
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
	
	/** Logout - this will invalidate any active JSESSION and will also invalidate your bearer token */
	public Future<Z5HttpResponse<Boolean>> logout() {
		return logout(null);
	}
	
	/** Logout - this will invalidate any active JSESSION and will also invalidate your bearer token */
	public Future<Z5HttpResponse<Boolean>> logout(Z5HttpResponseHandler<Boolean> handler) {
		return getClient().doGet(Types.BOOLEAN, Users.LOGOUT, new Z5HttpResponseHandler<Boolean>() {

			@Override
			public void onSuccess(int code, Boolean result) {
				if (result != null && result.booleanValue()) {
					getClient().setToken(null);
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
	
	/** Test if an email address is already registered in the system - true if the email already exists in the system */
	public Future<Z5HttpResponse<Boolean>> isEmailRegistered(String email) {
		return isEmailRegistered(email, null);
	}
	
	/** Test if an email address is already registered in the system - true if the email already exists in the system */
	public Future<Z5HttpResponse<Boolean>> isEmailRegistered(String email, Z5HttpResponseHandler<Boolean> handler) {
		return getClient().doPost(Types.BOOLEAN, Users.EMAIL_EXISTS, email, handler);
	}
	
	/** Get email validation status - S-Digital only */
	public Future<Z5HttpResponse<Map<String,Boolean>>> getEmailValidationStatus(String email) {
		return getEmailValidationStatus(email, null);
	}
	
	/** Get email validation status - S-Digital only - returns a map with keys of isVerified, Specialized_Terms and Specialized_Terms_Apps - ie is the user verified and have they accepted these terms & conditions */
	public Future<Z5HttpResponse<Map<String,Boolean>>> getEmailValidationStatus(String email, Z5HttpResponseHandler<Map<String,Boolean>> handler) {
		Map<String, Object> queryParams = new HashMap<>(1);
		queryParams.put("email", email);
		return getClient().doGet(Types.MAP_BOOLEAN, Users.EMAIL_STATUS, queryParams, handler);
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
			NewPassword request = new NewPassword();
			request.setOldPassword(oldPassword);
			request.setNewPassword(newPassword);
			return getClient().doPost(Types.VOID, Users.CHANGE_PASSWORD_SPECIALIZED, request, handler);
		} else {
			User u = new User();
			u.setPassword(newPassword);
			return getClient().doPost(Types.VOID, Users.SET_USER, u, handler);
		}
		
	}
	
	/** Refresh a bearer token - get a new token if the current one is nearing expiry */
	public Future<Z5HttpResponse<OAuthToken>> refreshToken() {
		return refreshToken(null);
	}
	
	/** Refresh a bearer token - get a new token if the current one is nearing expiry */
	public Future<Z5HttpResponse<OAuthToken>> refreshToken(Z5HttpResponseHandler<OAuthToken> handler) {
		return getClient().doGet(Types.OAUTHTOKENALT, Users.REFRESH_TOKEN, new Z5HttpResponseHandler<OAuthToken>() {

			@Override
			public void onSuccess(int code, OAuthToken result) {
				getClient().setToken(result);
				
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

	/** Return PasswordComplexity regex string */
	public Future<Z5HttpResponse<String>> passwordComplexity(){
		return passwordComplexity(null);
	}

	private Future<Z5HttpResponse<String>> passwordComplexity(Z5HttpResponseHandler handler){
		return getClient().doGet(Types.STRING, Users.PASSWORD_COMPLEXITY,null, handler);
	}

	/** Reconfirm email */
	public Future<Z5HttpResponse<Void>> reconfirm(String email){
		return reconfirm(email,null);
	}

	private Future<Z5HttpResponse<Void>> reconfirm(String email, Z5HttpResponseHandler handler){
		Map<String, Object> queryParams = new HashMap<>(1);
		queryParams.put("email", email);
		return getClient().doGet(Types.VOID, Users.RECONFIRM, queryParams, handler);
	}
}
