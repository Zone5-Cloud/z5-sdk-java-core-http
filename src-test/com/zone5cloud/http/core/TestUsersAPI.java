package com.zone5cloud.http.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

import com.zone5cloud.core.Z5Error;
import com.zone5cloud.core.enums.UnitMeasurement;
import com.zone5cloud.core.oauth.OAuthToken;
import com.zone5cloud.core.users.LoginRequest;
import com.zone5cloud.core.users.LoginResponse;
import com.zone5cloud.core.users.RegisterUser;
import com.zone5cloud.core.users.User;
import com.zone5cloud.core.users.UserPreferences;
import com.zone5cloud.http.core.api.UserAPI;
import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseHandler;

public class TestUsersAPI extends BaseTest {

	UserAPI api = new UserAPI();
	
	@Before
	public void setup() throws InterruptedException, ExecutionException {
		login();
	}
	
	@Test
	public void testLoginLogout() throws Exception {
		Z5HttpResponse<LoginResponse> response = api.login(TEST_EMAIL, TEST_PASSWORD, clientConfig.getClientID(), clientConfig.getClientSecret()).get();
		assertEquals(200, response.getStatusCode());
		LoginResponse login = response.getResult();
		assertNotNull(login);
		assertNotNull(login.getToken());
		assertEquals(login.getToken(), Z5HttpClient.get().getToken().getToken());
		
		Z5HttpResponse<Boolean> logout = api.logout().get();
		assertEquals(200, logout.getStatusCode());
		assertNotNull(logout.getResult());
		assertTrue(logout.getResult().booleanValue());
		assertNull(Z5HttpClient.get().getToken());
	}
	
	@Test
	public void testRegistrationNotLoggedIn() throws Exception {
		new UserAPI().logout().get(); //logout
		testRegistrationLoginDelete();
	}
	
	@Test
	public void testRegistrationLoggedIn() throws Exception {
		testMe(); // confirm logged in
		testRegistrationLoginDelete();
	}
	
	/** To run this test you need a valid clientId & secret */
	//@Test - run from the 2 above differing scenarios
	public void testRegistrationLoginDelete() throws Exception {
		String[] parts = TEST_EMAIL.split("@");
		String email = String.format("%s%s%d@%s", parts[0], (parts[0].contains("+") ? "" : "+"), System.currentTimeMillis(), parts[1]);
		String password = "superS3cretStu55";
		String firstname = "Test";
		String lastname = "User";
		
		RegisterUser register = new RegisterUser();
		register.setEmail(email);
		register.setPassword(password);
		register.setFirstname(firstname);
		register.setLastname(lastname);
		
		// For S-Digital registrations (optional)
		//register.setParams(new HashMap<String, String>(2));
		//register.getParams().put("regoSource", "Rider Hub");
		//register.getParams().put("regoKey", "<alternate GIGYA ACCESS KEY>");
		
		// optional - set weight, thresholds, dob, gender etc
		register.setWeight(80.1d);
		
		// check that this user does not yet exist in the system
		assertFalse(api.isEmailRegistered(email).get().getResult());
		
		User user = api.register(register).get().getResult();
		assertNotNull(user.getId()); // our unique userId
		assertEquals(email, user.getEmail());
		assertEquals(Locale.getDefault().toString().toLowerCase(), user.getLocale().toLowerCase());
		
		// Note - in S-Digital, the user will need to validate their email before they can login...
		if (api.getClient().isSpecialized()) {
			System.out.println("Waiting for confirmation that you have verified your email address ... press Enter when done");
			System.in.read();
		}
		
		// Login and set our bearer token
		LoginRequest request = new LoginRequest(email, password, clientConfig.getClientID(), clientConfig.getClientSecret());
		List<String> terms = new ArrayList<>();
		terms.add("Specialized_Terms_Apps");
		terms.add("Specialized_Terms");
		request.setAccept(terms);
		Future<Z5HttpResponse<LoginResponse>> f = api.login(request, null);
		LoginResponse r = f.get().getResult();
		assertNotNull(r.getToken());
		
		// Try it out!
		User me = api.me().get().getResult();
		assertEquals(me.getId(), user.getId());
		
		// check that this user is now considered registered
		assertTrue(api.isEmailRegistered(email).get().getResult());
		assertTrue(api.logout().get().getResult());
		assertNull(Z5HttpClient.get().getToken());
		
		assertTrue(api.isEmailRegistered(email).get().getResult());
		
		// Oops I forgot my password - send me an email with a magic link
		assertTrue(api.resetPassword(email).get().getResult());
		
		// Log back in
		f = api.login(email, password, clientConfig.getClientID(), clientConfig.getClientSecret());
		r = f.get().getResult();
		assertNotNull(r.getToken());
	
		me = api.me().get().getResult();
		assertEquals(me.getId(), user.getId());
		
		// Change my password and try it out
		assertEquals(200, api.changePassword(password, "myNewPassword123!!").get().getStatusCode());
		assertTrue(api.logout().get().getResult());
		
		f = api.login(email, "myNewPassword123!!", clientConfig.getClientID(), clientConfig.getClientSecret());
		r = f.get().getResult();
		assertNotNull(r.getToken());
		
		// Exercise the refresh access token
		if (api.getClient().isSpecialized() && api.getClient().getToken().getRefreshToken() == null) {
			OAuthToken alt = api.refreshToken().get().getResult();
			assertNotNull(alt.getToken());
			assertNotNull(alt.getTokenExp());
			me = api.me().get().getResult();
			assertEquals(me.getId(), user.getId());
		}

		
		// S-Digital Needs to be deleted via GIGYA
		if (!api.getClient().isSpecialized()) {
			// Delete this account
			assertEquals(204, api.deleteAccount(me.getId()).get().getStatusCode());
			int statusCode = 200;
			for(int i = 0; i < 5 && statusCode != 401; i++) {
				statusCode = api.me().get().getStatusCode();
			}
			
			// We are no longer valid!
			assertEquals(401, statusCode);
			assertEquals(401, api.login(email, password, clientConfig.getClientID(), clientConfig.getClientSecret()).get().getStatusCode());
		}
	}
	
	@Test
	public void testMe() throws Exception {
		
		Future<Z5HttpResponse<User>> f = api.me();
		Z5HttpResponse<User> r = f.get();
		assertEquals(200, r.getStatusCode());
		assertNotNull(r.getResult().getId());
		assertNotNull(r.getResult().getFirstname());
		assertNotNull(r.getResult().getLastname());
		assertNotNull(r.getResult().getEmail());
	}
	
	@Test
	public void testUserPreferences() throws Exception {
		
		Future<Z5HttpResponse<UserPreferences>> f = api.getUserPreferences(api.me().get().getResult().getId());
		Z5HttpResponse<UserPreferences> r = f.get();
		assertEquals(200, r.getStatusCode());
		assertNotNull(r.getResult().getMetric());
		
		UserPreferences p = new UserPreferences();
		p.setMetric(UnitMeasurement.imperial);
		assertEquals(true, api.setUserPreferences(p).get().getResult());
		assertEquals(UnitMeasurement.imperial, api.getUserPreferences(api.me().get().getResult().getId()).get().getResult().getMetric());
		
		p = new UserPreferences();
		p.setMetric(UnitMeasurement.metric);
		assertEquals(true, api.setUserPreferences(p).get().getResult());
		assertEquals(UnitMeasurement.metric, api.getUserPreferences(api.me().get().getResult().getId()).get().getResult().getMetric());
	}
	
	@Test
	public void testMeAsync() throws Exception {
		CountDownLatch l = new CountDownLatch(1);
		
		Future<Z5HttpResponse<User>> f = api.me(new Z5HttpResponseHandler<User>() {
			
			@Override
			public void onSuccess(int code, User result) {
				assertEquals(200, code);
				assertNotNull(result.getId());
				assertNotNull(result.getFirstname());
				assertNotNull(result.getLastname());
				assertNotNull(result.getEmail());
				l.countDown();
			}
			
			@Override
			public void onError(Throwable t, Z5Error error) {
				l.countDown();
				assertTrue(false);
				
			}
			
			@Override
			public void onError(int code, Z5Error error) {
				l.countDown();	
				assertTrue(false);
			}
		});
		
		f.get();
		l.await();
		
	}

	@Test
	public void testReconfirm() throws Exception {
		Z5HttpResponse<Void> response = api.reconfirm(TEST_EMAIL).get();
		assertTrue(response.isSuccess());
	}

	@Test
	public void testPasswordComplexityApi() throws Exception {
		Z5HttpResponse<String> response = api.passwordComplexity().get();
		assertNotNull(response.getResult());
		assertEquals("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", response.getResult());
	}

}
