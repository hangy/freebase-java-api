package com.narphorium.freebase.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.common.base.Preconditions;
import com.narphorium.freebase.services.exceptions.AuthenticationException;

public class AbstractFreebaseService {

	private static final Log LOG = LogFactory
			.getLog(AbstractFreebaseService.class);

	private final JsonFactory jsonFactory;

	private final String key;

	private final HttpRequestFactory httpRequestFactory;

	private URL baseUrl;

	protected AbstractFreebaseService(final String key,
			final HttpRequestFactory httpRequestFactory,
			final JsonFactory jsonFactory) {
		try {
			baseUrl = new URL("https://www.googleapis.com/freebase/v1");
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}

		this.key = key;
		this.httpRequestFactory = Preconditions
				.checkNotNull(httpRequestFactory);
		this.jsonFactory = Preconditions.checkNotNull(jsonFactory);
	}

	protected AbstractFreebaseService(final URL baseUrl, final String key,
			final HttpRequestFactory httpRequestFactory,
			final JsonFactory jsonFactory) {
		this.baseUrl = Preconditions.checkNotNull(baseUrl);
		this.key = key;
		this.httpRequestFactory = Preconditions
				.checkNotNull(httpRequestFactory);
		this.jsonFactory = Preconditions.checkNotNull(jsonFactory);
	}

	public final synchronized URL getBaseUrl() {
		return baseUrl;
	}

	protected final Object parseJSON(final String results) throws IOException {
		return jsonFactory.fromString(results, GenericJson.class);
	}

	protected final String generateJSON(final Object object) {
		return jsonFactory.toString(object);
	}

	protected final HttpRequest buildGetRequest(final String url)
			throws IOException {
		return buildGetRequest(new GenericUrl(formatUrl(url)));
	}

	protected final HttpRequest buildGetRequest(final GenericUrl url)
			throws IOException {
		return httpRequestFactory.buildGetRequest(addKeyToUrl(url));
	}

	protected final HttpRequest buildPostRequest(final String url,
			final HttpContent content) throws IOException,
			AuthenticationException {
		return buildPostRequest(new GenericUrl(formatUrl(url)), content);
	}

	protected final HttpRequest buildPostRequest(final GenericUrl url,
			final HttpContent content) throws IOException,
			AuthenticationException {
		final HttpRequestInitializer initializer = httpRequestFactory
				.getInitializer();
		if (null == initializer
				|| !(initializer instanceof GoogleAccessProtectedResource)) {
			throw new AuthenticationException();
		}

		return httpRequestFactory.buildPostRequest(addKeyToUrl(url), content);
	}

	protected final String fetchPage(final String url) throws IOException {
		final HttpRequest request = buildGetRequest(url);
		final String content = getExecutionResult(request);
		LOG.debug(content);

		return content;
	}

	protected final String fetchPage(final GenericUrl url) throws IOException {
		final HttpRequest request = buildGetRequest(url);
		final String content = getExecutionResult(request);
		LOG.debug(content);

		return content;
	}

	protected final String postContent(final URL url,
			final Map<String, String> content) throws AuthenticationException {
		try {
			final UrlEncodedContent c = new UrlEncodedContent(content);
			final HttpRequest request = buildPostRequest(url.toString(), c);

			return request.execute().parseAsString();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return "";
		}
	}

	protected final String uploadFile(final URL url, final byte[] content,
			final String contentType) throws AuthenticationException {
		final ByteArrayContent c = new ByteArrayContent(contentType, content);

		HttpRequest request;
		try {
			request = buildPostRequest(url.toString(), c);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return "";
		}

		final String result = getExecutionResult(request);
		LOG.debug(result);
		return result;
	}

	private String getExecutionResult(final HttpRequest request) {
		try {
			final String result = request.execute().parseAsString();
			LOG.debug(result);
			return result;
		} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
			return "";
		}
	}

	private String formatUrl(final String url) {
		return url.replaceAll(" ", "%20");
	}

	private GenericUrl addKeyToUrl(final GenericUrl url) {
		if (null != key && !url.containsKey("key")) {
			url.set("key", key);
		}

		return url;
	}

}
