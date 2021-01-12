package com.zone5cloud.http.core.responses;

import com.zone5cloud.core.Z5Error;

public interface Z5HttpResponseHandler<T> {
	
	public void onSuccess(int code, T result);
	
	public void onError(int code, Z5Error error);
	
	public void onError(Throwable t, Z5Error error);

}
