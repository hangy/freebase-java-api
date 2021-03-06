package com.narphorium.freebase.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

public class AbstractFreebaseService {

	protected static final String USER_AGENT = "Freebase Java API ("
			+ System.getProperty("os.name") + ")";
	private static final Log LOG = LogFactory
			.getLog(AbstractFreebaseService.class);

	private static final JsonFactory jsonFactory = new JacksonFactory();

	private final HttpClient httpClient;
	private final HttpContext localContext = new BasicHttpContext();
	private final CookieStore cookieStore = new BasicCookieStore();

	private URL baseUrl;

	protected AbstractFreebaseService(final HttpClient httpClient) {
		try {
			baseUrl = new URL("http://www.freebase.com/api");
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}

		if (null == httpClient) {
			throw new IllegalArgumentException("httpClient cannot be null");
		}

		this.httpClient = httpClient;
		this.localContext.setAttribute(ClientContext.COOKIE_STORE,
				this.cookieStore);
	}

	public AbstractFreebaseService(final URL baseUrl,
			final HttpClient httpClient) {
		if (null == baseUrl) {
			throw new IllegalArgumentException("baseUrl cannot be null");
		}

		if (null == httpClient) {
			throw new IllegalArgumentException("httpClient cannot be null");
		}

		this.baseUrl = baseUrl;
		this.httpClient = httpClient;
		this.localContext.setAttribute(ClientContext.COOKIE_STORE,
				this.cookieStore);
	}

	public final synchronized URL getBaseUrl() {
		return baseUrl;
	}

	/**
	 * Resets the <code>mwLastWriteTime</code> cookie to the current time. This
	 * ensures that any requests from the time you use this service gets you the
	 * latest data instead of possibly stale cached data.
	 * 
	 * @see http://www.freebase.com/docs/web_services/touch
	 * @return The Freebase.com response
	 */
	public final String touch() {
		String result = "";

		final HttpPost method = new HttpPost(this.baseUrl + "/service/touch");
		method.addHeader("User-Agent", USER_AGENT);
		method.addHeader("X-Metaweb-Request", "");
		method.addHeader("Accept-Charset", "utf-8");

		result = getExecutionResult(method);
		return result;
	}

	protected static final Object parseJSON(String results) throws IOException {
		return jsonFactory.fromString(results, GenericJson.class);
	}

	protected static final String generateJSON(Object object) {
		return jsonFactory.toString(object);
	}

	protected final String fetchPage(final String url) throws IOException {
		final String formattedUrl = url.replaceAll(" ", "%20");

		LOG.debug("URL: " + formattedUrl);

		final HttpGet method = new HttpGet(formattedUrl);
		method.addHeader("User-Agent", USER_AGENT);
		method.addHeader("X-Metaweb-Request", "");
		method.addHeader("Accept-Charset", "utf-8");

		final String content = getExecutionResult(method);
		LOG.debug(content);

		return content;
	}

	protected final String postContent(final URL url,
			final Map<String, String> content) {
		final HttpPost method = new HttpPost(url.toString());
		method.addHeader("User-Agent", USER_AGENT);
		method.addHeader("X-Metaweb-Request", "");
		method.addHeader("Accept-Charset", "utf-8");

		final List<NameValuePair> httpParams = new ArrayList<NameValuePair>(
				content.keySet().size());
		for (final String parameter : content.keySet()) {
			httpParams.add(new BasicNameValuePair(parameter, content
					.get(parameter)));
		}

		try {
			method.setEntity(new UrlEncodedFormEntity(httpParams, HTTP.UTF_8));
			return getExecutionResult(method);
		} catch (final UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
		}

		return "";
	}

	protected final String uploadFile(final URL url, final byte[] content,
			final String contentType) {
		final HttpPost method = new HttpPost(url.toString());
		method.addHeader("User-Agent", USER_AGENT);
		method.addHeader("X-Metaweb-Request", "");
		method.addHeader("Accept-Charset", "utf-8");
		method.addHeader("Content-Type", contentType);

		final ByteArrayEntity entity = new ByteArrayEntity(content);
		method.setEntity(entity);

		return getExecutionResult(method);
	}

	protected final <T> T executeHttpRequest(HttpUriRequest request,
			ResponseHandler<? extends T> responseHandler) throws IOException {
		return httpClient.execute(request, responseHandler, localContext);
	}

	private String getExecutionResult(final HttpUriRequest request) {
		try {
			final ResponseHandler<String> handler = new ResponseHandler<String>() {
				public String handleResponse(HttpResponse response)
						throws IOException {
					final int status = response.getStatusLine().getStatusCode();

					if (status != HttpStatus.SC_OK) {
						throw new IOException(status + ": "
								+ response.getStatusLine().getReasonPhrase());
					}

					final HttpEntity entity = response.getEntity();
					for (final Header header : response.getAllHeaders()) {
						LOG.info(header);
					}

					if (entity != null) {
						return EntityUtils.toString(entity, "utf8");
					} else {
						return "";
					}
				}
			};

			return executeHttpRequest(request, handler);
		} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
		}

		return "";
	}

}
