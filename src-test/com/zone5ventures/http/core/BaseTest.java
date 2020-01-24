package com.zone5ventures.http.core;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;

import com.zone5ventures.core.users.User;
import com.zone5ventures.core.utils.GsonManager;
import com.zone5ventures.http.core.api.UserAPI;

public abstract class BaseTest {
	
	/* SET YOUR OAUTH BEARER TOKEN HERE */
	protected String token = null;
	
	/* SET YOUR SERVER ENDPOINT HERE */
	protected String server = "staging.todaysplan.com.au";
	
	{
		// read token and server from ~/tp.env
		// token = ...
		// server = ...
		File f = new File(System.getProperty("user.home")+File.separatorChar+"tp.env");
		if (!f.exists())
			 f = new File(System.getProperty("user.home")+File.separatorChar+"z5.env");
		
		if (f.exists()) {
			try {
				for(String line : FileUtils.readLines(f, "UTF-8")) {
					String[] arr = line.split(" = ");
					if (arr.length == 2) {
						if (arr[0].trim().equals("token"))
							token = arr[1].trim();
						else if (arr[0].trim().equals("server"))
							server = arr[1].trim();
					}
				}
			} catch (Exception e) { }
			
			if (token != null || server != null)
				System.out.println(String.format("[ Using credentials in file %s - server=%s, token=%s ]", f.getAbsolutePath(), server, token));
		}
	}
	
	public String getBaseEndpoint() {
		if (server.startsWith("127.0.0.1"))
			return String.format("http://%s", server);
		return String.format("https://%s", server);
	}
	
	@Before
	public void init() {
		Z5HttpClient.get().setHostname(server);
		Z5HttpClient.get().setToken(token);
		Z5HttpClient.get().setDebug(true);
	}
	
	protected File createTempFile(String extn) throws IOException {
		File tmp = File.createTempFile(getClass().getSimpleName(), extn);
		tmp.deleteOnExit();
		return tmp;
	}
	
	public User me() throws ExecutionException, InterruptedException {
		return new UserAPI().me().get().getResult();
	}

	protected String toJson(Object o) {
		if  (o != null) {
			return GsonManager.getInstance(true).toJson(o);
		}
		return "null";
	}
}
