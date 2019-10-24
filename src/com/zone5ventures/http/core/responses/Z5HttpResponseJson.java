package com.zone5ventures.http.core.responses;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;

import com.zone5ventures.core.utils.GsonManager;

public class Z5HttpResponseJson<T> extends Z5HttpResponse<T> {

	private final Type t;
	private String js = null;
	
	public Z5HttpResponseJson(Type t, CloseableHttpResponse rsp) {
		super(rsp);
		this.t = t;
	}
	
	public Z5HttpResponseJson(Exception e) {
		super(e);
		this.t = null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected T deserialize(InputStream is) throws IOException {
		
		js = is == null ? null : IOUtils.toString(is, StandardCharsets.UTF_8);
			
		if (js == null || t == null || t == Void.class)
			return null;
			
		if (t == String.class)
			return (T)js;
			
		return GsonManager.getInstance().fromJson(js, t);
	}
	
	@Override
	public String toString() {
		if (js != null && js.startsWith("{"))
			return GsonManager.getInstance(true).toJson(GsonManager.getInstance().fromJson(js, Map.class));
		else if (js != null && js.startsWith("["))
			return GsonManager.getInstance(true).toJson(GsonManager.getInstance().fromJson(js, ArrayList.class));
		else if (js != null)
			return js;
		else if (error != null && error.startsWith("{"))
			return GsonManager.getInstance(true).toJson(GsonManager.getInstance().fromJson(error, Map.class));
		else if (error != null && error.startsWith("["))
			return GsonManager.getInstance(true).toJson(GsonManager.getInstance().fromJson(error, ArrayList.class));
		else if (error != null)
			return error;
		return "";
	}
}
