package com.zone5ventures.http.core;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.zone5ventures.http.core.requests.Z5HttpGet;
import com.zone5ventures.http.core.requests.Z5HttpGetDownload;
import com.zone5ventures.http.core.requests.Z5HttpPost;
import com.zone5ventures.http.core.requests.Z5HttpPostFileUpload;
import com.zone5ventures.http.core.requests.Z5HttpPostForm;
import com.zone5ventures.http.core.requests.Z5HttpRequest;
import com.zone5ventures.http.core.responses.Z5HttpResponse;
import com.zone5ventures.http.core.responses.Z5HttpResponseHandler;
import com.zone5ventures.http.core.responses.Z5HttpResponseJson;

public class Z5HttpClient implements Closeable {
		
	public static ThreadLocal<Z5HttpClient> THREADLOCAL = new ThreadLocal<>();
	
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
	
	private String bearer = null;
	private ILogger logger = null;
	
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
	
	/** Enable verbose debug logging */
	public void setDebug(boolean on) {
		debug = on;
	}
	
	/** Set a user's OAuth bearer token */
	public void setToken(String token) {
		this.bearer = token == null ? null : String.format("Bearer %s", token);
	}
	
	/** Get the user's OAuth bearer token - note that this will include the Bearer prefix */
	public String getBearer() {
		return this.bearer;
	}
	
	/** Set an alternate logger */
	public void setLogger(ILogger logger) {
		this.logger = logger;
	}
	
	/** Set the server hostname - ie staging.todaysplan.com.au */
	public void setHostname(String hostname) {
		this.hostname = hostname;
		
		if (hostname != null && (hostname.startsWith("127.0.0.1") || hostname.contains("8080")))
			this.protocol = "http";
		else
			this.protocol = "https";
	}
	
	/** Add authorization header, and also set the legacy tp-nodecorate header */
	protected void decorate(Z5HttpRequest<?> req) {
		if (bearer != null)
			req.addHeader("Authorization", bearer);
		
		req.addHeader("tp-nodecorate", "true");
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
						result = req.newInstance(e);
						
					} finally {
						if (rsp != null)
							try { rsp.close(); } catch (IOException e) { }
									
						req.releaseConnection();
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
	
	public <T> Future<Z5HttpResponse<T>> doGet(Type t, String path, Z5HttpResponseHandler<T> handler) {
		Z5HttpGet<T> req = new Z5HttpGet<>(t, getURL(path));
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
	
	
	
	
	
	
	/** A application/x-www-form-urlencoded post 
	public <T> void doPostForm(Type cls, String path, Map<String, String> form, Z5HttpResponse<T> callback) throws IOException {
		
		String url = getURL(path);
		
		HttpPost post = new HttpPost(url);
		CloseableHttpResponse rsp = null;
		try {
		
			StringBuilder result = new StringBuilder();
		    boolean first = true;
		    for(Entry<String, String> ent : form.entrySet()) {
		        if (first)
		            first = false;
		        else
		            result.append("&");    
		        result.append(URLEncoder.encode(ent.getKey(), "UTF-8"));
		        result.append("=");
		        result.append(URLEncoder.encode(ent.getValue(), "UTF-8"));
		    }    
		    
		    if (debug)
				logger.info("POST %s %s ", url, result.toString());
		    		    
		    StringEntity js = new StringEntity(result.toString(), "UTF-8");
			post.addHeader("content-type", "application/x-www-form-urlencoded");
			post.setEntity(js);
			
			decorate(post);
			
			rsp = client.execute(post);
			
			doResponse(cls, rsp, callback);
			
		} catch (Exception e) {
			callback.onError(e, null);
							
		} finally {
			
			if (rsp != null)
				try { rsp.close(); } catch (IOException e) { }
			
			post.releaseConnection();
		}
			
	} */
	
	@Override
	public void close() {
		IOUtils.closeQuietly(client);
	}
	
	protected String getURL(String path, Object ...args) {
		String uri = String.format(path, args);
		
		if (!uri.startsWith("/"))
			uri = String.format("/%s", path);
		
		return String.format("%s://%s%s", protocol, hostname, uri);
	}
	
	
}
