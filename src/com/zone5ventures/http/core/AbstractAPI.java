package com.zone5ventures.http.core;

/**
 * Just a simple wrapper to accessing the Z5HttpClient. 
 * 
 * Allows for a specific client to be injected, or the default ThreadLocal instance can be used.
 *
 */
public abstract class AbstractAPI {
	
	private Z5HttpClient client = null;
	
	public void setClient(Z5HttpClient client) {
		this.client = client;
	}
	
	public Z5HttpClient getClient() {
		return client == null ? Z5HttpClient.get() : client;
	}
}
