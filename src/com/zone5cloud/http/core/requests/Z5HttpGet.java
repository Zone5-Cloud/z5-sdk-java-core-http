package com.zone5cloud.http.core.requests;

import java.lang.reflect.Type;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseJson;

public class Z5HttpGet<T> extends HttpGet implements Z5HttpRequest<T> {
	
	private final Type t;
	
	public Z5HttpGet(Type t, String url) {
		super(url);
		this.t = t;
	}
	
	@Override
	public String toString() {
		return String.format("GET %s ", getURI().getPath());
	}
	
	@Override
	public Z5HttpResponse<T> newInstance(CloseableHttpResponse rsp) {
		return new Z5HttpResponseJson<>(t, rsp);
	}
	
	@Override
	public Z5HttpResponse<T> newInstance(Exception e) {
		return new Z5HttpResponseJson<>(e);
	}

}
