package com.zone5ventures.http.core.responses;

public interface Z5HttpResponseHandler<T> {
	
	public void onSuccess(int code, T result);
	
	public void onError(int code, String error);
	
	public void onError(Throwable t, String error);

}
