package com.zone5ventures.http.core.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import com.zone5ventures.core.Types;
import com.zone5ventures.core.enums.UserConnectionsType;
import com.zone5ventures.core.thirdpartyconnections.ThirdPartyToken;
import com.zone5ventures.core.thirdpartyconnections.ThirdPartyTokenResponse;
import com.zone5ventures.core.users.Users;
import com.zone5ventures.http.core.AbstractAPI;
import com.zone5ventures.http.core.responses.Z5HttpResponse;
import com.zone5ventures.http.core.responses.Z5HttpResponseHandler;

/**
 * Endpoints related to setting, retrieving and removing third party OAuth bearer tokens
 *
 */
public class ThirdPartyConnectionsAPI extends AbstractAPI {
	
	private static final String SERVICE_NAME_QUERY_PARAM = "service_name";
		
	public Future<Z5HttpResponse<ThirdPartyTokenResponse>> set_third_party_token(UserConnectionsType type, ThirdPartyToken connection) {
		return set_third_party_token(type, connection, null);
	}
	
	public Future<Z5HttpResponse<ThirdPartyTokenResponse>> set_third_party_token(UserConnectionsType type, ThirdPartyToken connection, Z5HttpResponseHandler<ThirdPartyTokenResponse> handler) {
		Map<String, Object> queryParams = new HashMap<>(1);
		queryParams.put(SERVICE_NAME_QUERY_PARAM, type);
		return getClient().doPost(Types.THIRD_PARTY_TOKEN_RESPONSE, Users.SET_THIRD_PARTY_CONNECTION, connection, queryParams, handler);
	}
	
	public Future<Z5HttpResponse<ThirdPartyTokenResponse>> has_third_party_token(UserConnectionsType type) {
		return has_third_party_token(type, null);
	}
	
	public Future<Z5HttpResponse<ThirdPartyTokenResponse>> has_third_party_token(UserConnectionsType type, Z5HttpResponseHandler<ThirdPartyTokenResponse> handler) {
		Map<String, Object> queryParams = new HashMap<>(1);
		queryParams.put(SERVICE_NAME_QUERY_PARAM, type);
		return getClient().doGet(Types.THIRD_PARTY_TOKEN_RESPONSE, Users.HAS_THIRD_PARTY_CONNECTION, queryParams, handler);
	}
	
	public Future<Z5HttpResponse<ThirdPartyTokenResponse>> remove_third_party_token(UserConnectionsType type) {
		return remove_third_party_token(type, null);
	}
	
	public Future<Z5HttpResponse<ThirdPartyTokenResponse>> remove_third_party_token(UserConnectionsType type, Z5HttpResponseHandler<ThirdPartyTokenResponse> handler) {
		Map<String, Object> queryParams = new HashMap<>(1);
		queryParams.put(SERVICE_NAME_QUERY_PARAM, type);
		return getClient().doPost(Types.THIRD_PARTY_TOKEN_RESPONSE, Users.REM_THIRD_PARTY_CONNECTION, null, queryParams, handler);
	}
}
