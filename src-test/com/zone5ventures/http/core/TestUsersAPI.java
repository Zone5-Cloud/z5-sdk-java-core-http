package com.zone5ventures.http.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.junit.Test;

import com.zone5ventures.common.users.User;
import com.zone5ventures.http.core.api.UserAPI;
import com.zone5ventures.http.core.responses.Z5HttpResponse;
import com.zone5ventures.http.core.responses.Z5HttpResponseHandler;

public class TestUsersAPI extends BaseTest {

	UserAPI api = new UserAPI();
	
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
			public void onError(Throwable t, String error) {
				l.countDown();
				assertTrue(false);
				
			}
			
			@Override
			public void onError(int code, String error) {
				l.countDown();	
				assertTrue(false);
			}
		});
		
		f.get();
		l.await();
		
	}

}
