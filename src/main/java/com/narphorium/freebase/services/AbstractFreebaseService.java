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
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.stringtree.json.JSONReader;
import org.stringtree.json.JSONWriter;

import com.narphorium.freebase.auth.Authorizer;

public class AbstractFreebaseService {

	protected static final String USER_AGENT = "Freebase Java API ("
			+ System.getProperty("os.name") + ")";

	private static final int MAXIMUM_RETRIES = 3;

	private static final Log LOG = LogFactory
			.getLog(AbstractFreebaseService.class);

	private static final JSONReader JSON_PARSER = new JSONReader();
	private static final JSONWriter JSON_WRITER = new JSONWriter();

	private final String key;
	private final Authorizer authorizer;

	private final HttpClient httpClient;
	private final HttpContext localContext = new BasicHttpContext();
	private final CookieStore cookieStore = new BasicCookieStore();

	private URL baseUrl;
	private int maximumRetries = MAXIMUM_RETRIES;
	private int currentTry = Integer.MIN_VALUE;

	protected AbstractFreebaseService(final String key,
			final HttpClient httpClient) {
		try {
			baseUrl = new URL("https://www.googleapis.com/freebase/v1");
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}

		if (null == httpClient) {
			throw new IllegalArgumentException("httpClient cannot be null");
		}

		this.key = key;
		this.authorizer = null;
		this.httpClient = httpClient;
		this.localContext.setAttribute(ClientContext.COOKIE_STORE,
				this.cookieStore);
	}

	protected AbstractFreebaseService(final URL baseUrl, final String key,
			final HttpClient httpClient) {
		if (null == baseUrl) {
			throw new IllegalArgumentException("baseUrl cannot be null");
		}

		if (null == httpClient) {
			throw new IllegalArgumentException("httpClient cannot be null");
		}

		this.baseUrl = baseUrl;
		this.key = key;
		this.authorizer = null;
		this.httpClient = httpClient;
		this.localContext.setAttribute(ClientContext.COOKIE_STORE,
				this.cookieStore);
	}

	protected AbstractFreebaseService(final String key,
			final Authorizer authorizer, final HttpClient httpClient) {
		try {
			baseUrl = new URL("https://www.googleapis.com/freebase/v1");
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}

		if (null == authorizer) {
			throw new IllegalArgumentException("authorizer cannot be null");
		}

		if (null == httpClient) {
			throw new IllegalArgumentException("httpClient cannot be null");
		}

		this.key = key;
		this.authorizer = authorizer;
		this.httpClient = httpClient;
		this.localContext.setAttribute(ClientContext.COOKIE_STORE,
				this.cookieStore);
	}

	protected AbstractFreebaseService(final URL baseUrl, final String key,
			final Authorizer authorizer, final HttpClient httpClient) {
		if (null == baseUrl) {
			throw new IllegalArgumentException("baseUrl cannot be null");
		}

		if (null == authorizer) {
			throw new IllegalArgumentException("authorizer cannot be null");
		}

		if (null == httpClient) {
			throw new IllegalArgumentException("httpClient cannot be null");
		}

		this.baseUrl = baseUrl;
		this.key = key;
		this.authorizer = authorizer;
		this.httpClient = httpClient;
		this.localContext.setAttribute(ClientContext.COOKIE_STORE,
				this.cookieStore);
	}

	public final synchronized URL getBaseUrl() {
		return baseUrl;
	}

	public final int getMaximumRetries() {
		return maximumRetries;
	}

	public final void setMaximumRetries(final int maximumRetries) {
		if (1 > maximumRetries) {
			throw new IllegalArgumentException(
					"maximumRetries must be 1 or higher");
		}

		this.maximumRetries = maximumRetries;
	}

	protected static final Object parseJSON(String results) throws IOException {
		return JSON_PARSER.read(results);
	}

	protected static final String generateJSON(Object object) {
		return JSON_WRITER.write(object);
	}

	protected final String fetchPage(final String url) throws IOException {
		final String formattedUrl = addKeyToUrl(url.replaceAll(" ", "%20"));

		LOG.debug("URL: " + formattedUrl);

		final HttpGet method = new HttpGet(formattedUrl);
		addDefaultHeaders(method);

		final String content = getExecutionResult(method);
		LOG.debug(content);

		return content;
	}

	protected final String postContent(final URL url,
			final Map<String, String> content) {
		final HttpPost method = new HttpPost(addKeyToUrl(url.toString()));
		addDefaultHeaders(method);

		final List<NameValuePair> httpParams = new ArrayList<NameValuePair>(
				content.keySet().size());
		for (final String parameter : content.keySet()) {
			httpParams.add(new BasicNameValuePair(parameter, content
					.get(parameter)));
		}

		try {
			method.setEntity(new UrlEncodedFormEntity(httpParams, HTTP.UTF_8));
			final String result = getExecutionResult(method);
			LOG.debug(content);
			return result;
		} catch (final UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
			return "";
		}
	}

	protected final String uploadFile(final URL url, final byte[] content,
			final String contentType) {
		final HttpPost method = new HttpPost(addKeyToUrl(url.toString()));
		addDefaultHeaders(method);
		method.addHeader("Content-Type", contentType);

		final ByteArrayEntity entity = new ByteArrayEntity(content);
		method.setEntity(entity);

		final String result = getExecutionResult(method);
		LOG.debug(result);
		return result;
	}

	protected final <T> T executeHttpRequest(HttpUriRequest request,
			ResponseHandler<? extends T> responseHandler) throws IOException {
		return httpClient.execute(request, responseHandler, localContext);
	}

	private synchronized String getExecutionResult(final HttpUriRequest request) {
		try {
			currentTry = 0;
			final ResponseHandler<String> handler = new ResponseHandler<String>() {
				public String handleResponse(HttpResponse response)
						throws IOException {
					++currentTry;
					final int status = response.getStatusLine().getStatusCode();

					if (HttpStatus.SC_UNAUTHORIZED == status) {
						if (null == authorizer || currentTry >= maximumRetries) {
							throw new IOException(status
									+ ": "
									+ response.getStatusLine()
											.getReasonPhrase() + " (after "
									+ currentTry + " retries)");
						}

						authorizer.refresh();
						return executeHttpRequest(request, this);
					}

					if (HttpStatus.SC_OK != status) {
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

			final String result = executeHttpRequest(request, handler);
			LOG.debug(result);
			return result;
		} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
			return "";
		} finally {
			currentTry = Integer.MIN_VALUE;
		}
	}

	private void addDefaultHeaders(final AbstractHttpMessage message) {
		message.addHeader("User-Agent", USER_AGENT);
		message.addHeader("X-Metaweb-Request", "");
		message.addHeader("Accept-Charset", "utf-8");

		if (null != authorizer) {
			message.addHeader("Authorization",
					"OAuth " + authorizer.getAccessToken());
		}
	}

	private String addKeyToUrl(final String url) {
		if (null == key) {
			return url;
		}

		return url + "&key=" + key;
	}

}
