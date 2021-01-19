package com.zone5cloud.http.core.requests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.zone5cloud.core.Types;
import com.zone5cloud.core.enums.GrantType;
import com.zone5cloud.core.oauth.OAuthToken;
import com.zone5cloud.core.oauth.OAuthTokenRequest;
import com.zone5cloud.core.users.Users;

public class TestZ5HttpPostForm {

	@Test
	public void testEncoding() {
		OAuthTokenRequest request = new OAuthTokenRequest();
		request.setUsername("testuser@gmail.com");
		request.setPassword("pword123");
		request.setGrantType(GrantType.USERNAME_PASSWORD);
		request.setClientId("test-client-id");
		request.setClientSecret("test-secret");
		request.setRedirect("redirect_test");
		
		Z5HttpPostForm<OAuthToken> form1 = new Z5HttpPostForm<>(Types.OAUTHTOKEN, Users.LOGIN, request);
		
		
		Map<String, String> m = new HashMap<>();
		m.put("username", "testuser@gmail.com");
		m.put("password", "pword123");
		m.put("grant_type", "password");
		m.put("client_id", "test-client-id");
		m.put("client_secret", "test-secret");
		m.put("redirect_uri", "redirect_test");
		
		Z5HttpPostForm<OAuthToken> form2 = new Z5HttpPostForm<>(Types.OAUTHTOKEN, Users.LOGIN, m);
		
		assertEquals(form1.getEntity().toString(), form2.getEntity().toString());
		//assertEquals(form1.str, form2.str); // can't control the order
	}
}
