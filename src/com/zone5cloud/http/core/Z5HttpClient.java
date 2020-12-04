package com.zone5cloud.http.core;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.zone5cloud.core.Endpoints;
import com.zone5cloud.core.Types;
import com.zone5cloud.core.Z5AuthorizationDelegate;
import com.zone5cloud.core.Z5Error;
import com.zone5cloud.core.enums.GrantType;
import com.zone5cloud.core.enums.Z5HttpHeader;
import com.zone5cloud.core.oauth.AuthToken;
import com.zone5cloud.core.oauth.OAuthToken;
import com.zone5cloud.core.oauth.OAuthTokenRequest;
import com.zone5cloud.core.users.Users;
import com.zone5cloud.http.core.requests.Z5HttpDelete;
import com.zone5cloud.http.core.requests.Z5HttpGet;
import com.zone5cloud.http.core.requests.Z5HttpGetDownload;
import com.zone5cloud.http.core.requests.Z5HttpPost;
import com.zone5cloud.http.core.requests.Z5HttpPostFileUpload;
import com.zone5cloud.http.core.requests.Z5HttpPostForm;
import com.zone5cloud.http.core.requests.Z5HttpPut;
import com.zone5cloud.http.core.requests.Z5HttpRequest;
import com.zone5cloud.http.core.responses.Z5HttpResponse;
import com.zone5cloud.http.core.responses.Z5HttpResponseHandler;
import com.zone5cloud.http.core.responses.Z5HttpResponseJson;

public class Z5HttpClient implements Closeable {
		
	public static final ThreadLocal<Z5HttpClient> THREADLOCAL = new ThreadLocal<>();
	
	public static Z5HttpClient get() {
		Z5HttpClient c = THREADLOCAL.get();
		if (c == null) {
			c = new Z5HttpClient();
			THREADLOCAL.set(c);
		}
		return c;
	}
	
	private String hostname = "staging.todaysplan.com.au";
	private String protocol = "https";
	
	private final AtomicReference<AuthToken> authToken = new AtomicReference<>(null);
	private String userAgent = null;
	private String clientID = null;
	private String clientSecret = null;
	
	private ILogger logger = null;
	protected final Set<Z5AuthorizationDelegate> delegates = new HashSet<>();
	private final ExecutorService delegateExecutor = Executors.newSingleThreadExecutor();
	private final Object setTokenLock = new Object();
	private final Object refreshLock = new Object();
	
	// this is a threadsafe client
	private final CloseableHttpClient client; 
	
	private boolean debug = false;
	
	/** Uses a default HttpClient. For production apps, you can use the other constructor to pass in your own HttpClient instance */
	public Z5HttpClient() {
		
		this(HttpClients.createDefault(), new ILogger() {
			
			@Override
			public void info(String fmt, Object... args) {
				try {
					System.out.println(String.format(fmt, args));
				} catch (Exception e) {
					
				}
			}
			
			@Override
			public void error(Throwable t) {
				t.printStackTrace();
				
			}
		});
	}
	
	/** Allow for a custom HttpClient and logger to be used */
	public Z5HttpClient(CloseableHttpClient client, ILogger logger) {
		this.client = client;
		this.logger = logger;
	}
	
	public boolean isSpecialized() {
		return this.hostname != null && (this.hostname.equals("api-sp.todaysplan.com.au") || this.hostname.equals("api-sp-staging.todaysplan.com.au"));
	}
	
	/** Enable verbose debug logging */
	public void setDebug(boolean on) {
		debug = on;
	}
	
	/** Set a user's OAuth bearer token */
	public void setToken(AuthToken token) {
		synchronized(setTokenLock) {
			AuthToken oldToken = this.authToken.getAndSet(token);
			if ((token == null && oldToken != null) || (token != null && !token.equals(oldToken))) {
				delegateExecutor.execute(new Runnable() {
					public void run() {
						for (Z5AuthorizationDelegate delegate: delegates) {
							delegate.onAuthTokenUpdated(token);
						}
					}
				});
			}
		}
	}
	
	public AuthToken getToken() {
		return this.authToken.get();
	}
	
	public void subscribe(Z5AuthorizationDelegate delegate) {
		this.delegates.add(delegate);
	}
	
	public void unsubscribe(Z5AuthorizationDelegate delegate) {
		this.delegates.remove(delegate);
	}
	
	/** Set a user-agent string */
	public void setUserAgent(String agent) {
		this.userAgent = agent;
	}
	
	/** Get the user-agent string */
	public String getUserAgent() {
		return this.userAgent;
	}
	
	public void setClientIDAndSecret(String clientID, String secret) {
		this.clientID = clientID;
		this.clientSecret = secret;
	}
	
	/** Set an alternate logger */
	public void setLogger(ILogger logger) {
		this.logger = logger;
	}
	
	/** Set the server hostname - ie staging.todaysplan.com.au */
	public void setHostname(String hostname) {
		this.hostname = hostname;
		
		if (hostname != null && (hostname.startsWith("127.0.0.1") || hostname.contains(":8080")))
			this.protocol = "http";
		else
			this.protocol = "https";
	}
	
	/**
	 * Add headers: 
	 * * add authorization header
	 * * add the legacy tp-nodecorate header 
	 * * add the customisable User-Agent header if set
	 * @throws InterruptedException 
	 **/
	protected void decorate(Z5HttpRequest<?> req) throws InterruptedException {
		AuthToken token = this.authToken.get();
		if (token != null && token.getBearer() != null && Endpoints.requiresAuth(req.getURI().getPath())) {
			refreshTokenIfRequired();
			// fetch again cos it may have been updated
			
			token = this.authToken.get();
			if (token != null && token.getBearer() != null) {
				req.addHeader(Z5HttpHeader.AUTHORIZATION.toString(), token.getBearer());
			}
		}
		
		req.addHeader(Z5HttpHeader.TP_NO_DECORATE.toString(), "true");
		req.addHeader(Z5HttpHeader.API_KEY.toString(), clientID);
		req.addHeader(Z5HttpHeader.API_KEY_SECRET.toString(), clientSecret);
		
		String agent = this.userAgent;
		if (agent != null && !agent.isEmpty()) {
			req.addHeader(Z5HttpHeader.USER_AGENT.toString(), agent);
		}
	}
	
	private void refreshTokenIfRequired() throws InterruptedException {
		AuthToken token = this.authToken.get();
		if (token != null && token.getRefreshToken() != null && token.isExpired()) {
			// need to refresh
			synchronized(refreshLock) {
				// refetch and check token as it might have refreshed while we were waiting for mutex
				token = this.authToken.get();
				if (token != null && token.getRefreshToken() != null && token.isExpired()) {
					String username = token.extractUsername();
					if (username != null) {
						OAuthTokenRequest request = new OAuthTokenRequest();
						request.setUsername(token.extractUsername());
						request.setRefreshToken(token.getRefreshToken());
						request.setClientId(clientID);
						request.setClientSecret(clientSecret);
						request.setGrantType(GrantType.REFRESH_TOKEN);
						
						try {
							doFormPost(Types.OAUTHTOKEN, Users.NEW_ACCESS_TOKEN, request, new Z5HttpResponseHandler<OAuthToken>() {
								@Override
								public void onSuccess(int code, OAuthToken result) {
									if (result.getExpiresIn() != null) {
				        				// calculate expiry based on system clock and expiresIn seconds
				        				result.setTokenExp(System.currentTimeMillis() + (result.getExpiresIn() * 1000));
				        			}
									setToken(result);
								}
		
								@Override
								public void onError(int code, Z5Error error) {
									// error
									logger.info("Failed to refresh token: %s", error.getMessage());
								}
		
								@Override
								public void onError(Throwable t, Z5Error error) {
									logger.error(t);
								}
							}).get();
						} catch (ExecutionException e) {
							logger.error(e);
						}
					}
				}
			}
		}
	}
	
	/** 
	 * Actually make the HTTP request.
	 * 
	 * The result / error can be passed through via the handler or via the future.
	 * 
	 * The handler may be null.
	 * 
	 * @param req
	 * @param handler (not required)
	 * @return
	 */
	protected <T> Future<Z5HttpResponse<T>> invokeAsync(Z5HttpRequest<T> req, Z5HttpResponseHandler<T> handler) {
		
		final AtomicBoolean isCancelled = new AtomicBoolean(false);
		final AtomicBoolean isDone = new AtomicBoolean(false);
		
		final AtomicReference<Z5HttpResponse<T>> resultRef = new AtomicReference<>(null);
		
		return new Future<Z5HttpResponse<T>>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				if (!isCancelled.getAndSet(true))
					try { req.abort(); } catch (Exception e) { }
				return true;
			}

			@Override
			public boolean isCancelled() {
				return isCancelled.get(); 
			}

			@Override
			public boolean isDone() {
				return isCancelled.get() || isDone.get();
			}

			@Override
			public Z5HttpResponse<T> get() throws InterruptedException, ExecutionException {
				
				if (resultRef.get() != null)
					return resultRef.get();
				
				synchronized(resultRef) {
					
					if (resultRef.get() != null)
						return resultRef.get();
					
					decorate(req);
					
					CloseableHttpResponse rsp = null;
					Z5HttpResponse<T> result =  null;
					
					try {
						
						if (debug)
							logger.info("%s", req.toString());
						
						rsp = client.execute((HttpUriRequest)req);
						result = req.newInstance(rsp);
						result.parse();
					
					} catch (Exception e) {
						e.printStackTrace();
						result = req.newInstance(e);
						
					} finally {
						if (rsp != null)
							try { rsp.close(); } catch (IOException e) { }
					}
					
					if (debug)
						logger.info("%s", result.toString());
					
					resultRef.set(result);
					
					if (handler != null) {
						if (result.getResult() != null || result.isSuccess())
							handler.onSuccess(result.getStatusCode(), result.getResult());
						else if (result.getException() != null)
							handler.onError(result.getException(), result.getError());
						else
							handler.onError(result.getStatusCode(), result.getError());
					}

					return result;
				}
			}

			@Override
			public Z5HttpResponseJson<T> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
				throw new ExecutionException("This method is not supported", null);
			}
		};
	}
		
	public <T> Future<Z5HttpResponse<T>> doPost(Type t, String path, Object entity, Z5HttpResponseHandler<T> handler) {
		Z5HttpPost<T> req = new Z5HttpPost<>(t, getURL(path), entity);
		return invokeAsync(req, handler);
	}
	
	public <T> Future<Z5HttpResponse<T>> doPut(Type t, String path, Object entity, Z5HttpResponseHandler<T> handler) {
		Z5HttpPut<T> req = new Z5HttpPut<>(t, getURL(path), entity);
		return invokeAsync(req, handler);
	}
	
	public <T> Future<Z5HttpResponse<T>> doPost(Type t, String path, Object entity, Map<String,Object> queryParams, Z5HttpResponseHandler<T> handler) {
		Z5HttpPost<T> req = new Z5HttpPost<>(t, getURL(path, queryParams), entity);
		return invokeAsync(req, handler);
	}
	
	public <T> Future<Z5HttpResponse<T>> doGet(Type t, String path, Z5HttpResponseHandler<T> handler) {
		Z5HttpGet<T> req = new Z5HttpGet<>(t, getURL(path));
		return invokeAsync(req, handler);
	}
	
	public <T> Future<Z5HttpResponse<T>> doDelete(Type t, String path, Z5HttpResponseHandler<T> handler) {
		Z5HttpDelete<T> req = new Z5HttpDelete<>(t, getURL(path));
		return invokeAsync(req, handler);
	}
	
	public <T> Future<Z5HttpResponse<T>> doGet(Type t, String path, Map<String,Object> queryParams, Z5HttpResponseHandler<T> handler) {
		Z5HttpGet<T> req = new Z5HttpGet<>(t, getURL(path, queryParams, new Object[0]));
		return invokeAsync(req, handler);
	}
		
	public <T> Future<Z5HttpResponse<T>> doUpload(Type t, String path, Object entity, File file, Z5HttpResponseHandler<T> handler) {
		Z5HttpPostFileUpload<T> req = new Z5HttpPostFileUpload<>(t, getURL(path), entity, file);
		return invokeAsync(req, handler);
	}
	
	public Future<Z5HttpResponse<File>> doDownload(String path, File tgt, Z5HttpResponseHandler<File> handler) {
		Z5HttpGetDownload req = new Z5HttpGetDownload(getURL(path), tgt);
		return invokeAsync(req, handler);
	}
	
	public Future<Z5HttpResponse<File>> doDownload(String path, String extn, Z5HttpResponseHandler<File> handler) {
		Z5HttpGetDownload req = new Z5HttpGetDownload(getURL(path), extn);
		return invokeAsync(req, handler);
	}
	
	public <T> Future<Z5HttpResponse<T>> doFormPost(Type t, String path, Map<String, String> form, Z5HttpResponseHandler<T> handler) {
		Z5HttpPostForm<T> req = new Z5HttpPostForm<>(t, getURL(path), form);
		return invokeAsync(req, handler);
	}
	
	public <T> Future<Z5HttpResponse<T>> doFormPost(Type t, String path, Object form, Z5HttpResponseHandler<T> handler) {
		Z5HttpPostForm<T> req = new Z5HttpPostForm<>(t, getURL(path), form);
		return invokeAsync(req, handler);
	}
	
	@Override
	public void close() {
		if (client != null) {
			try { client.close(); } catch (IOException e) { }
		}
		
		this.delegates.clear();
	}
	
	protected String getURL(String path, Object ...args) {
		String uri = String.format(path, args);
		
		if (!uri.startsWith("/"))
			uri = String.format("/%s", path);
		
		return String.format("%s://%s%s", protocol, hostname, uri);
	}
	
	protected String getURL(String path, Map<String,Object> queryParams, Object ...args) {
		String url = getURL(path, args);
		
		if (queryParams != null && !queryParams.isEmpty()) {
			StringWriter sw = new StringWriter();
			for(Map.Entry<String, Object> entry : queryParams.entrySet()) {
				if (!sw.toString().isEmpty())
					sw.append("&");
				try {
					sw.append(String.format("%s=%s", entry.getKey(), URLEncoder.encode(entry.getValue().toString(), "UTF-8")));
				} catch (UnsupportedEncodingException e) {
					// ignore
				}
			}
			return String.format("%s?%s", url, sw.toString());
		}
		
		return url;
	}
	
}
