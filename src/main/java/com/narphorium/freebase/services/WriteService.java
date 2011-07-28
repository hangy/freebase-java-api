package com.narphorium.freebase.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpRequestFactory;
import com.narphorium.freebase.query.Query;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class WriteService extends AbstractFreebaseService {

	private static final Log LOG = LogFactory.getLog(WriteService.class);

	public WriteService(final String key,
			final HttpRequestFactory httpRequestFactory,
			final JsonFactory jsonFactory) {
		super(key, httpRequestFactory, jsonFactory);
	}

	public WriteService(final URL baseUrl, final String key,
			final HttpRequestFactory httpRequestFactory,
			final JsonFactory jsonFactory) {
		super(baseUrl, key, httpRequestFactory, jsonFactory);
	}

	public final String write(final Query query)
			throws FreebaseServiceException {
		try {
			final URL url = new URL(getBaseUrl() + "/mqlwrite");
			final Map<String, String> content = new HashMap<String, String>();
			content.put("query", query.toJSON());
			return postContent(url, content);
		} catch (final MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}

		return null;
	}
}
