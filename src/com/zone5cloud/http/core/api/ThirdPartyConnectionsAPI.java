package com.zone5cloud.http.core.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import com.zone5cloud.core.Types;
import com.zone5cloud.core.enums.UserConnectionsType;
import com.zone5cloud.core.thirdpartyconnections.PushRegistration;
import com.zone5cloud.core.thirdpartyconnections.PushRegistrationResponse;
import com.zone5cloud.core.thirdpartyconnections.ThirdPartyToken;
import com.zone5cloud.core.thirdpartyconnections.ThirdPartyTokenResponse;
import com.zone5cloud.core.thirdpartyconnections.UpgradeAvailableResponse;
import com.zone5cloud.core.users.Users;
import com.zone5cloud.http.core.AbstractAPI;
import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseHandler;

/**
 * Endpoints related to setting, retrieving and removing third party OAuth bearer tokens
 *
 */
public class ThirdPartyConnectionsAPI extends AbstractAPI {
	
	private static final String SERVICE_NAME_QUERY_PARAM = "service_name";
		
	public Future<Z5HttpResponse<ThirdPartyTokenResponse>> setThirdPartyToken(UserConnectionsType type, ThirdPartyToken connection) {
		return setThirdPartyToken(type, connection, null);
	}
	
	public Future<Z5HttpResponse<ThirdPartyTokenResponse>> setThirdPartyToken(UserConnectionsType type, ThirdPartyToken connection, Z5HttpResponseHandler<ThirdPartyTokenResponse> handler) {
		Map<String, Object> queryParams = new HashMap<>(1);
		queryParams.put(SERVICE_NAME_QUERY_PARAM, type);
		return getClient().doPost(Types.THIRD_PARTY_TOKEN_RESPONSE, Users.SET_THIRD_PARTY_CONNECTION, connection, queryParams, handler);
	}
	
	public Future<Z5HttpResponse<ThirdPartyTokenResponse>> hasThirdPartyToken(UserConnectionsType type) {
		return hasThirdPartyToken(type, null);
	}
	
	public Future<Z5HttpResponse<ThirdPartyTokenResponse>> hasThirdPartyToken(UserConnectionsType type, Z5HttpResponseHandler<ThirdPartyTokenResponse> handler) {
		Map<String, Object> queryParams = new HashMap<>(1);
		queryParams.put(SERVICE_NAME_QUERY_PARAM, type);
		return getClient().doGet(Types.THIRD_PARTY_TOKEN_RESPONSE, Users.HAS_THIRD_PARTY_CONNECTION, queryParams, handler);
	}
	
	public Future<Z5HttpResponse<ThirdPartyTokenResponse>> removeThirdPartyToken(UserConnectionsType type) {
		return removeThirdPartyToken(type, null);
	}
	
	public Future<Z5HttpResponse<ThirdPartyTokenResponse>> removeThirdPartyToken(UserConnectionsType type, Z5HttpResponseHandler<ThirdPartyTokenResponse> handler) {
		Map<String, Object> queryParams = new HashMap<>(1);
		queryParams.put(SERVICE_NAME_QUERY_PARAM, type);
		return getClient().doPost(Types.THIRD_PARTY_TOKEN_RESPONSE, Users.REM_THIRD_PARTY_CONNECTION, null, queryParams, handler);
	}
	
	/** 
	 * Register a push token for a device with a 3rd party
	 * @param PushRegistration (token, platform, deviceId)
	 **/
	public Future<Z5HttpResponse<PushRegistrationResponse>> registerDeviceWithThirdParty(PushRegistration registration) {
		return registerDeviceWithThirdParty(registration, null);
	}
	
	/** 
	 * Register a push token for a device with a 3rd party
	 * @param PushRegistration (token, platform, deviceId)
	 * @param handler: callback on asynchronous completion
	 **/
	public Future<Z5HttpResponse<PushRegistrationResponse>> registerDeviceWithThirdParty(PushRegistration registration, Z5HttpResponseHandler<PushRegistrationResponse> handler) {
		return getClient().doPost(Types.PUSH_REGISTRATION_RESPONSE, Users.REGISTER_DEVICE_THIRD_PARTY_CONNECTION, registration, null, handler);
	}
	
    /** 
     * Deregister a push token for a device with a 3rd party 
     * @param 3rd party push token to deregister
     * @returns Void response. It response status is OK then the registration was deleted.
     * Response will be 400 if the registration does not exist or if the user does not have permission to delete it.
     */
	public Future<Z5HttpResponse<Void>> deregisterDeviceWithThirdParty(String token) {
		return deregisterDeviceWithThirdParty(token, null);
	}
	
	/** 
	 * Deregister a push token for a device with a 3rd party 
     * @param 3rd party push token to deregister
     * @returns Void response. It response status is OK then the registration was deleted.
     * Response will be 400 if the registration does not exist or if the user does not have permission to delete it.
	 * @param handler: callback on asynchronous completion
	 **/
	public Future<Z5HttpResponse<Void>> deregisterDeviceWithThirdParty(String token, Z5HttpResponseHandler<Void> handler) {
		return getClient().doDelete(Types.VOID, Users.DEREGISTER_DEVICE_THIRD_PARTY_CONNECTION.replace("{token}", token), handler);
	}
	
	/**
	 * Query whether the current version of the user agent (client app) has been deprecated and requires an upgrade.
	 */
	public Future<Z5HttpResponse<UpgradeAvailableResponse>> getDeprecated() {
		return getDeprecated(null);
	}
	
	/**
	 * Query whether the current version of the user agent (client app) has been deprecated and requires an upgrade.
	 * @param handler: callback on asynchronous completion
	 */
	public Future<Z5HttpResponse<UpgradeAvailableResponse>> getDeprecated(Z5HttpResponseHandler<UpgradeAvailableResponse> handler) {
		return getClient().doGet(Types.UPGRADE_AVAILABLE_RESPONSE, Users.GET_DEPRECATED, handler);
	}
}
