package com.zone5ventures.http.core.api;

import java.util.concurrent.Future;

import com.zone5ventures.common.Types;
import com.zone5ventures.common.users.User;
import com.zone5ventures.common.users.Users;
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
}
