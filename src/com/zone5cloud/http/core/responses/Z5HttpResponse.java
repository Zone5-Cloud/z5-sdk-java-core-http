package com.zone5cloud.http.core.responses;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;

import com.zone5cloud.core.Types;
import com.zone5cloud.core.Z5Error;
import com.zone5cloud.core.utils.GsonManager;

public abstract class Z5HttpResponse<T> {
	
	private final int code;
	private final CloseableHttpResponse rsp;
		
	// Deserialized object (if successful result)
	private T t = null;
	
	// Raw response - usually a string, unless we are downloading a file
	protected Z5Error error = null;
	protected String rawError = null;
	
	protected Exception e = null;
	
	public Z5HttpResponse(CloseableHttpResponse rsp) {
		this.code = rsp.getStatusLine().getStatusCode();
		this.rsp = rsp;
	}
	
	public Z5HttpResponse(Exception e) {
		this.code = -1;
		this.rsp = null;
	}
	
	public boolean isSuccess() {
		return this.code >= 200 && this.code < 300;
	}
	
	public void parse() throws IOException {
		
		if (rsp.getEntity() == null || rsp.getEntity().getContent() == null)
			return;
		
		if (isSuccess()) {
			this.t = rsp.getEntity() != null && rsp.getEntity().getContent() != null ? deserialize(rsp.getEntity().getContent()) : null;
		
		} else {
			this.rawError = IOUtils.toString(rsp.getEntity().getContent(), StandardCharsets.UTF_8);
			this.error = GsonManager.getInstance().fromJson(rawError, Types.ERROR);
		}
	}
	
	public int getStatusCode() {
		return code;
	}
	
	public T getResult() {
		return this.t;
	}
	
	public Z5Error getError() {
		return error;
	}
	
	public Exception getException() {
		return e;
	}
	
	protected abstract T deserialize(InputStream is) throws IOException;
}
