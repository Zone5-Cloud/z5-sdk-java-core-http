package com.zone5cloud.http.core.requests;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseFile;

public class Z5HttpGetDownload extends HttpGet implements Z5HttpRequest<File> {
	
	private File file = null;
	
	public Z5HttpGetDownload(String url, String extn) {
		super(url);
		try {
			this.file = File.createTempFile("download", extn == null ? "tmp" : extn);
		} catch (IOException e) {
			
		}
	}
	
	public Z5HttpGetDownload(String url, File file) {
		super(url);
		this.file = file;
	}
	
	@Override
	public String toString() {
		return String.format("GET %s ", getURI().getPath());
	}
	
	@Override
	public Z5HttpResponse<File> newInstance(CloseableHttpResponse rsp) {
		return new Z5HttpResponseFile(rsp, file);
	}
	
	@Override
	public Z5HttpResponse<File> newInstance(Exception e) {
		return new Z5HttpResponseFile(e);
	}

}
