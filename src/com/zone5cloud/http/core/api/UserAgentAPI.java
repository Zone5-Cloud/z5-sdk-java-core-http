package com.zone5cloud.http.core.api;

import java.util.concurrent.Future;

import com.zone5cloud.core.Types;
import com.zone5cloud.core.thirdpartyconnections.UpgradeAvailableResponse;
import com.zone5cloud.core.useragent.UserAgent;
import com.zone5cloud.http.core.AbstractAPI;
import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseHandler;

public class UserAgentAPI extends AbstractAPI {
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
		return getClient().doGet(Types.UPGRADE_AVAILABLE_RESPONSE, UserAgent.GET_DEPRECATED, handler);
	}
}
