package com.zone5cloud.http.core.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.zone5cloud.core.Z5Error;
import com.zone5cloud.core.users.LoginResponse;
import com.zone5cloud.http.core.BaseTest;
import com.zone5cloud.http.core.Z5HttpClient;
import com.zone5cloud.http.core.api.UserAPI;
import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseHandler;

public class TestUtilities extends BaseTest {
	private UserAPI api = new UserAPI();

	@Test
	public void testErrors() throws Exception {
		Z5HttpClient.get().setClientIDAndSecret("bogus clientid", "bogus secret");
		Z5HttpResponse<LoginResponse> response = api.login(TEST_EMAIL, TEST_PASSWORD, "bogus clientid", "bogus secret", new Z5HttpResponseHandler<LoginResponse>() {
			
			@Override
			public void onSuccess(int code, LoginResponse result) {
				assertTrue("should not have a successful request", false);
			}
			
			@Override
			public void onError(Throwable t, Z5Error error) {
				validateError(error);
			}
			
			@Override
			public void onError(int code, Z5Error error) {
				validateError(error);
			}
		}).get();
		
		assertEquals(401, response.getStatusCode());
		validateError(response.getError());
	}
	
	private void validateError(Z5Error error) {
		assertNotNull(error);
		assertNotNull(error.getMessage());
		assertNotNull(error.getError());
		
		assertEquals("true", error.getError());
		assertEquals("Token can not be issued - unsupported client_id", error.getMessage());
		
		assertNotNull(error.getErrors());
		assertEquals(1, error.getErrors().size());
		assertEquals("clientId", error.getErrorItem(0).getField());
		assertEquals(401105, error.getErrorItem(0).getCode().intValue());
		assertEquals("INVALID_CLIENT_ID_OR_SECRET", error.getErrorItem(0).getMessage());
		
	}

}
