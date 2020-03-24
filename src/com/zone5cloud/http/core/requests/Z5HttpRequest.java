package com.zone5cloud.http.core.requests;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import com.zone5cloud.http.core.responses.Z5HttpResponse;

public interface Z5HttpRequest<T> extends HttpUriRequest {
	
	public void addHeader(String key, String value);
	
	//public void abort();
	
	public void releaseConnection();
	
	public String toString();

	public Z5HttpResponse<T> newInstance(CloseableHttpResponse rsp); 
	public Z5HttpResponse<T> newInstance(Exception e); 
}
