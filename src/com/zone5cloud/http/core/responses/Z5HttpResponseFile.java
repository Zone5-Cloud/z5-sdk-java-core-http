package com.zone5cloud.http.core.responses;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;

import com.zone5cloud.core.utils.GsonManager;

public class Z5HttpResponseFile extends Z5HttpResponse<File> {
	
	private final File file;
	
	public Z5HttpResponseFile(CloseableHttpResponse rsp, File file) {
		super(rsp);
		this.file = file;
	}
	
	public Z5HttpResponseFile(Exception e) {
		super(e);
		this.file = null;
	}
	
	@Override
	protected File deserialize(InputStream is) throws IOException {
		if (file != null) {
			Files.deleteIfExists(file.toPath());
			FileUtils.copyInputStreamToFile(is, file);
		}
		return file;
	}
	
	@Override
	public String toString() {
		if (file != null) {
			return file.getAbsolutePath() + " (" + file.length()+")";
		} else if (error != null) {
			return GsonManager.getInstance(true).toJson(error);
		} else if (rawError != null && rawError.startsWith("{")) {
			return GsonManager.getInstance(true).toJson(GsonManager.getInstance().fromJson(rawError, Map.class));
		} else if (rawError != null && rawError.startsWith("[")) {
			return GsonManager.getInstance(true).toJson(GsonManager.getInstance().fromJson(rawError, ArrayList.class));
		} else if (rawError != null) {
			return rawError;
		}
		
		return "";
	}
}
