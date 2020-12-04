package com.zone5cloud.http.core;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Before;

import com.zone5cloud.core.users.User;
import com.zone5cloud.core.utils.GsonManager;
import com.zone5cloud.http.core.api.UserAPI;

public abstract class BaseTest {
	public static final String SBC_NO_VERIFICATION_GIGYA = "3_GoZ3q9P513xf8qjJuTkCQcLikOlWesA3lzES8cfPoGpXQfqrbONzu4pniGcNssqr";
	public static final String TP_COGNITO_KEY = "1er3227s1mia3pkqrngntl4sv6";
	public static final String TP_COGNITO_SECRET = "19re5046mf15n5m38klrmnr9sjtcia4sdv4hpn0ivoshm1tu72cp";
	public static final String TP_STAGING = "staging.todaysplan.com.au";
	public static final String SBC_STAGING = "api-sp-staging.todaysplan.com.au";
	public static final String JEANS_LOCAL = "192.168.1.17:8080";
	
	protected String TEST_EMAIL = "please-enter-a-user@todaysplan.com.au";
	protected String TEST_PASSWORD = "please-enter-your-password";
	protected String TEST_CLIENT_ID = TP_COGNITO_KEY;
	protected String TEST_CLIENT_SECRET = TP_COGNITO_SECRET;
	protected String BIKE_UUID = ""; // andrew SBC staging "d584c5cb-e81f-4fbe-bc0d-667e9bcd2c4c"
	
	protected void login() throws InterruptedException, ExecutionException {
		new UserAPI().login(TEST_EMAIL, TEST_PASSWORD, TEST_CLIENT_ID, TEST_CLIENT_SECRET).get();
	}
	
	/* SET YOUR SERVER ENDPOINT HERE */
	protected String TEST_SERVER = TP_STAGING;
	
	
	public String getBaseEndpoint() {
		if (TEST_SERVER.startsWith("127.0.0.1"))
			return String.format("http://%s", TEST_SERVER);
		return String.format("https://%s", TEST_SERVER);
	}
	
	@Before
	public void init() {
		Z5HttpClient.get().setHostname(TEST_SERVER);
		Z5HttpClient.get().setClientIDAndSecret(TEST_CLIENT_ID, TEST_CLIENT_SECRET);
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
