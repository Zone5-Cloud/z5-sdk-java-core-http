package com.zone5ventures.http.core.requests;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.zone5ventures.http.core.responses.Z5HttpResponse;
import com.zone5ventures.http.core.responses.Z5HttpResponseJson;

public class Z5HttpPostForm<T> extends HttpPost implements Z5HttpRequest<T> {
	
	private final String str;
	private final Type t;
	
	public Z5HttpPostForm(Type t, String url, Map<String, String> form) {
		super(url);
		
		StringBuilder result = new StringBuilder();
	    boolean first = true;
	    for(Entry<String, String> ent : form.entrySet()) {
	        if (first)
	            first = false;
	        else
	            result.append("&");    
	        try {
		        result.append(URLEncoder.encode(ent.getKey(), StandardCharsets.UTF_8.name()));
		        result.append("=");
		        result.append(URLEncoder.encode(ent.getValue(), StandardCharsets.UTF_8.name()));
	        } catch (UnsupportedEncodingException e) {
	        	
	        }
	    }    
	    
	    str = result.toString();
		
	    StringEntity js = new StringEntity(result.toString(), StandardCharsets.UTF_8.name());
		addHeader("content-type", "application/x-www-form-urlencoded");
		setEntity(js);
		
		this.t = t;
	}
	
	@Override
	public String toString() {
		return String.format("POST %s x-www-form-urlencoded: %s", getURI().getPath(), str);
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
