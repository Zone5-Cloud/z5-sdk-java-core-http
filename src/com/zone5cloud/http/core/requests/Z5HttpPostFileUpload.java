package com.zone5cloud.http.core.requests;

import java.io.File;
import java.lang.reflect.Type;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import com.zone5cloud.core.utils.GsonManager;
import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseJson;

public class Z5HttpPostFileUpload<T> extends HttpPost implements Z5HttpRequest<T> {
	
	private final Object entity;
	private final File file;
	private final Type t;
	
	public Z5HttpPostFileUpload(Type t, String url, Object entity, File file) {
		super(url);
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		FileBody fileBody = new FileBody(file);
		builder.addPart("attachment", fileBody);
		builder.addTextBody("filename", file.getName());
			
		if (entity != null)
			builder.addTextBody("json", GsonManager.getInstance().toJson(entity));
			
		setEntity(builder.build());
		
		this.entity = entity;
		this.file = file;
		this.t = t;
	}
	
	@Override
	public String toString() {
		return String.format("POST %s multipart-form: attachment=<%d bytes>, filename=%s, json=%s", getURI().getPath(), file.length(), file.getName(), entity == null ? "" : GsonManager.getInstance(true).toJson(entity));
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
