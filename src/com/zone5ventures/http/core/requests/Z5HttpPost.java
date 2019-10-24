package com.zone5ventures.http.core.requests;

import java.lang.reflect.Type;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.zone5ventures.http.core.responses.Z5HttpResponse;
import com.zone5ventures.http.core.responses.Z5HttpResponseJson;
import com.zone5ventures.utils.GsonManager;

public class Z5HttpPost<T> extends HttpPost implements Z5HttpRequest<T> {
	
	private final Object entity;
	private final Type t;
	
	public Z5HttpPost(Type t, String url, Object entity) {
		super(url);
		this.entity = entity;
		this.t = t;
		
		if (entity != null) {
			StringEntity js = new StringEntity(GsonManager.getInstance().toJson(entity), "UTF-8");
			addHeader("content-type", "application/json");
			setEntity(js);
		}
	}
	
	@Override
	public String toString() {
		return String.format("POST %s %s", getURI().getPath(), entity == null ? "" : GsonManager.getInstance(true).toJson(entity));
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
