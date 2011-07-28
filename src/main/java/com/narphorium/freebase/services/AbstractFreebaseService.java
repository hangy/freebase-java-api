package com.narphorium.freebase.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.UrlEncodedContent;
import com.google.common.base.Preconditions;

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
		return jsonFactory.fromString(results, Object.class);
	}

	protected final String generateJSON(final Object object) {
		return jsonFactory.toString(object);
	}

	protected final HttpRequest buildGetRequest(final String url)
			throws IOException {
		return httpRequestFactory.buildGetRequest(addKeyToUrl(new GenericUrl(
				url)));
	}

	protected final HttpRequest buildPostRequest(final String url,
			final HttpContent content) throws IOException {
		return httpRequestFactory.buildPostRequest(addKeyToUrl(new GenericUrl(
				url)), content);
	}

	protected final String fetchPage(final String url) throws IOException {
		final String formattedUrl = url.replaceAll(" ", "%20");

		LOG.debug("URL: " + formattedUrl);

		final HttpRequest request = buildGetRequest(formattedUrl);
		final String content = getExecutionResult(request);
		LOG.debug(content);

		return content;
	}

	protected final String postContent(final URL url,
			final Map<String, String> content) {
		try {
			final UrlEncodedContent c = new UrlEncodedContent();
			c.data = content;
			final HttpRequest request = buildPostRequest(url.toString(), c);

			return request.execute().parseAsString();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return "";
		}
	}

	protected final String uploadFile(final URL url, final byte[] content,
			final String contentType) {

		final ByteArrayContent c = new ByteArrayContent(content);
		c.type = contentType;

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

	private GenericUrl addKeyToUrl(final GenericUrl url) {
		if (null != key && !url.containsKey("key")) {
			url.set("key", key);
		}

		return url;
	}

}
