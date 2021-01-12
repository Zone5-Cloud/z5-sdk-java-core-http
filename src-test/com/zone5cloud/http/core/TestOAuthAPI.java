package com.zone5cloud.http.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import com.zone5cloud.core.Z5AuthorizationDelegate;
import com.zone5cloud.core.oauth.AuthToken;
import com.zone5cloud.core.oauth.OAuthToken;
import com.zone5cloud.core.users.LoginResponse;
import com.zone5cloud.core.users.User;
import com.zone5cloud.http.core.api.OAuthAPI;
import com.zone5cloud.http.core.api.UserAPI;

public class TestOAuthAPI extends BaseTest {

	OAuthAPI api = new OAuthAPI();
	UserAPI user = new UserAPI();
	
	@Test
	public void testGetUserToken() throws Exception {
		OAuthToken response = api.newAccessToken(TEST_CLIENT_ID, TEST_CLIENT_SECRET, "", TEST_EMAIL, TEST_PASSWORD).get().getResult();
		assertNotNull(response.getToken());
		
		User me = user.me().get().getResult();
		assertEquals(me.getEmail(), TEST_EMAIL);
	}
	
	@Test
	public void testRefreshUserToken() throws Exception {
		UserAPI users = new UserAPI();
		
		LoginResponse login = user.login(TEST_EMAIL, TEST_PASSWORD, TEST_CLIENT_ID, TEST_CLIENT_SECRET).get().getResult();
		User me = users.me().get().getResult();
		assertEquals(me.getEmail(), TEST_EMAIL);
		
		assertNotNull(Z5HttpClient.get().getToken());
		assertNotNull(Z5HttpClient.get().getToken().getToken());
		assertNotNull(Z5HttpClient.get().getToken().getTokenExp());
		if (login.getRefresh() != null) {
			assertNotNull(Z5HttpClient.get().getToken().getRefreshToken());
		}
		
		if (login.getRefresh() != null) {
			OAuthToken response = api.refreshAccessToken(TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_EMAIL, login.getRefresh()).get().getResult();
			assertNotNull(response.getToken());
		} else if (Z5HttpClient.get().isSpecialized()) {
			OAuthToken response = users.refreshToken().get().getResult();
			assertNotNull(response.getToken());
		} else {
			OAuthToken response = api.newAccessToken(TEST_CLIENT_ID, TEST_CLIENT_SECRET, "", TEST_EMAIL, TEST_PASSWORD).get().getResult();
			assertNotNull(response.getToken());
		}
		
		me = users.me().get().getResult();
		assertEquals(me.getEmail(), TEST_EMAIL);
		
		assertNotNull(Z5HttpClient.get().getToken());
	}
	
	@Test
	public void testAutoRefresh() throws IOException, InterruptedException, ExecutionException {
		login();
		// TODO test this with proper refresh
		AuthToken currentToken = Z5HttpClient.get().getToken();
		
		// expire the token to force the refresh sequence
		OAuthToken expiredToken = new OAuthToken();
		expiredToken.setToken(currentToken.getToken());
		expiredToken.setRefreshToken(currentToken.getRefreshToken());
		expiredToken.setTokenExp(System.currentTimeMillis() - 1);
		Z5HttpClient.get().setToken(expiredToken);
		
		User me = user.me().get().getResult();
		long id = me.getId();
		
		// check token has been updated
		AuthToken newToken = Z5HttpClient.get().getToken();
		
		if (newToken.getRefreshToken() != null) {
			assertNotEquals(currentToken.getToken(), newToken.getToken());
			assertEquals(currentToken.getRefreshToken(), newToken.getRefreshToken());
			assertTrue(newToken.getTokenExp() > System.currentTimeMillis() + 30000);
		}
		
		me = user.me().get().getResult();
		assertEquals(id, me.getId().longValue());
	}

	@Test
	public void testDelegate() throws InterruptedException {
		final AtomicBoolean d1 = new AtomicBoolean(false);
		final AtomicBoolean d2 = new AtomicBoolean(false);
		final Semaphore s1 = new Semaphore(0);
		final Semaphore s2 = new Semaphore(0);
		final AtomicBoolean shouldTrigger = new AtomicBoolean(false);
		Z5HttpClient client = new Z5HttpClient();
		
		Z5AuthorizationDelegate delegate1 = new Z5AuthorizationDelegate() {
			
			@Override
			public void onAuthTokenUpdated(AuthToken token) {
				d1.set(true);
				s1.release();
			}
		};
		
		Z5AuthorizationDelegate delegate2 = new Z5AuthorizationDelegate() {
			
			@Override
			public void onAuthTokenUpdated(AuthToken token) {
				d2.set(true);
				if (!shouldTrigger.get()) {
					assertFalse("delegate should not trigger", true);
				}
				s2.release();
			}
		};
		
		client.subscribe(delegate1);
		client.subscribe(delegate2);
		
		assertEquals(2, client.delegates.size());
		
		client.unsubscribe(delegate2);
		
		assertEquals(1, client.delegates.size());
		assertTrue(client.delegates.contains(delegate1));
		client.setToken(new OAuthToken());
		s1.acquire();
		
		assertTrue(d1.get());
		assertFalse(d2.get());
		// reset for next test
		d1.set(false);
		
		client.subscribe(delegate2);
		assertEquals(2, client.delegates.size());
		shouldTrigger.set(true);
		assertTrue(client.delegates.contains(delegate1));
		assertTrue(client.delegates.contains(delegate2));
		
		assertFalse(d1.get());
		assertFalse(d2.get());
		client.setToken(null);
		s1.acquire();
		s2.acquire();
		assertTrue(d1.get());
		assertTrue(d2.get());	
		
		client.close();
	}
	
	
	
	@Test
	public void testDelegateOrder() throws InterruptedException, ExecutionException {
		final ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> changes = new ConcurrentHashMap<>();
		final Semaphore semaphore = new Semaphore(-39);
		final Z5HttpClient client = new Z5HttpClient();
		
		Z5AuthorizationDelegate delegate = new Z5AuthorizationDelegate() {
			
			@Override
			public void onAuthTokenUpdated(AuthToken token) {
				String[] sender = token.getToken().split(":");
				changes.putIfAbsent(sender[0], new ConcurrentLinkedQueue<Long>());
				changes.get(sender[0]).add(Long.decode(sender[1]));
				System.out.println(sender[0] + ":" + sender[1]);
				semaphore.release();
			}
		};
		
		try {
			client.subscribe(delegate);
			
			class Run implements Callable<String> {
				private final String name;
				
				Run(String name) {
					this.name = name;
				}
				
				@Override
				public String call() {
					for (int i = 0; i < 10; i++) {
						OAuthToken token = new OAuthToken();
						token.setToken(name + ":" + i);
						client.setToken(token);
					}
					return name;
				}
			}
			
			ExecutorService executor = Executors.newFixedThreadPool(5);
			Set<Run> tasks = new HashSet<>();
			tasks.add(new Run("a"));
			tasks.add(new Run("b"));
			tasks.add(new Run("c"));
			tasks.add(new Run("d"));
			
			executor.invokeAll(tasks);
			semaphore.acquire();
			
			
			for (Run r: tasks) {
				ConcurrentLinkedQueue<Long> list = changes.get(r.name);
				Long previous = -1l;
				for (Long l: list) {
					System.out.println(r.name + ": " + l);
					assertTrue(l > previous);
					previous = l;
				}
			}
		} finally {
			client.unsubscribe(delegate);
			client.close();
		}
	}
	
	@Test
	public void testAdhocToken() throws InterruptedException, ExecutionException {
		login();
		
		// only applicable on SBC servers
		if (Z5HttpClient.get().isSpecialized()) {
			OAuthToken token = api.adhocAccessToken("wahooride").get().getResult();
			assertNotNull("Returned token should not be null", token);
			assertNotNull("Token should be valid", token.getToken());
			assertNotNull("Token should have an expiry", token.getExpiresIn());
			assertNotNull("Token should havea  scope", token.getScope());
		}
	}
}
