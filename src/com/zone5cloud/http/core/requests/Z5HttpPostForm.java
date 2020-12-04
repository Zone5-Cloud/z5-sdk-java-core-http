package com.zone5cloud.http.core.requests;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.google.gson.annotations.SerializedName;
import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseJson;

public class Z5HttpPostForm<T> extends HttpPost implements Z5HttpRequest<T> {
	
	protected final String str;
	private final Type t;
	
	public Z5HttpPostForm(Type t, String url, Object obj) {
		super(url);
		this.t = t;
		
		StringBuilder entity = new StringBuilder();
	    boolean first = true;
	    Field fields[] = obj.getClass().getDeclaredFields();
    	if (fields != null) {
    		for (Field f: fields) {    			
    			SerializedName ann = f.getAnnotation(SerializedName.class);
    			String name = ann != null ? ann.value() : f.getName();
    			try {
    				Object value = PropertyUtils.getProperty(obj, f.getName());
    				
    				// only include non null values
    				if (value != null) {
    					if (first) {
    	    	            first = false;
    	    			} else {
    	    				entity.append("&"); 
    	    			}
    					
	    				entity.append(URLEncoder.encode(name, StandardCharsets.UTF_8.name()));
	    				entity.append("=");
	    				entity.append(URLEncoder.encode(value.toString(), StandardCharsets.UTF_8.name()));
    				}
    			} catch(UnsupportedEncodingException|NoSuchMethodException|InvocationTargetException|IllegalAccessException e) {
    				
    			}
    		}
    	}
    	
    	str = entity.toString();
		
    	setEntity();
	}
	
	public Z5HttpPostForm(Type t, String url, Map<String, String> form) {
		super(url);
		this.t = t;
		
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
		
		setEntity();
	}
	
	private void setEntity() {
		StringEntity js = new StringEntity(str, StandardCharsets.UTF_8.name());
		addHeader("content-type", "application/x-www-form-urlencoded");
		setEntity(js);
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
