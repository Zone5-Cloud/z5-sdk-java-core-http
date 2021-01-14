package com.zone5cloud.http.core;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.zone5cloud.core.ClientConfig;
import org.apache.commons.io.FileUtils;
import org.junit.Before;

import com.zone5cloud.core.users.User;
import com.zone5cloud.core.utils.GsonManager;
import com.zone5cloud.http.core.api.UserAPI;

public abstract class BaseTest {
	/* SET YOUR TEST EMAIL HERE */
	protected String TEST_EMAIL = "<enter-your-email-here@todaysplan.com.au>";
	protected String TEST_PASSWORD = "<enter-your-password-here>";
	protected String TEST_BIKE_UUID = null; // andrew SBC Staging: "d584c5cb-e81f-4fbe-bc0d-667e9bcd2c4c"
	
	/* SET YOUR SERVER ENDPOINT HERE */
	protected String TEST_SERVER = "";
	// This is your allocated clientId and secret - these can be set to null for S-Digital environments
	protected ClientConfig clientConfig = new ClientConfig();
	
    public BaseTest() {
    		// read config ~/tp.env or ~/z5.env
    		File f = new File(System.getProperty("user.home")+File.separatorChar+"tp.env");
    		if (!f.exists())
    			 f = new File(System.getProperty("user.home")+File.separatorChar+"z5.env");
    		
    		if (f.exists()) {
    			try {
    				for(String line : FileUtils.readLines(f, "UTF-8")) {
    					String[] arr = line.split("=");
    					if (arr.length == 2) {
    						String key = arr[0].trim();
    						String value = arr[1].trim();
    						switch(key) {
    						case "username":
    							TEST_EMAIL = value;
								clientConfig.setUserName(value);
    							break;
    						case "password":
    							TEST_PASSWORD = value;
    							break;
    						case "server":
    							TEST_SERVER = value;
    							break;
    						case "clientID":
								clientConfig.setClientID(value);
    							break;
    						case "clientSecret":
								clientConfig.setClientSecret(value);
    							break;
    						}
    					}
    				}
    			} catch (Exception e) { }
    			
    			if (f.exists() && clientConfig.getUserName() != null || TEST_SERVER != null)
    				System.out.println(String.format("[ Using credentials in file %s - server=%s, username=%s ]",
							f.getAbsolutePath(), TEST_SERVER, clientConfig.getUserName()));
    		}
    }
    
	protected void login() throws InterruptedException, ExecutionException {
		new UserAPI().login(TEST_EMAIL, TEST_PASSWORD, clientConfig.getClientID(), clientConfig.getClientSecret()).get();
	}

	public String getBaseEndpoint() {
		if (TEST_SERVER.startsWith("127.0.0.1"))
			return String.format("http://%s", TEST_SERVER);
		return String.format("https://%s", TEST_SERVER);
	}
	
	@Before
	public void init() {
    	Z5HttpClient.get().setClientConfig(clientConfig);
		Z5HttpClient.get().setHostname(TEST_SERVER);
		Z5HttpClient.get().setClientIDAndSecret(clientConfig.getClientID(), clientConfig.getClientSecret());
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
