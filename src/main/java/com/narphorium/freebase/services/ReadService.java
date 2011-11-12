package com.narphorium.freebase.services;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.json.JsonFactory;
import com.narphorium.freebase.query.Query;
import com.narphorium.freebase.results.ResultSet;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class ReadService extends AbstractFreebaseService {

	public ReadService(final HttpRequestFactory httpRequestFactory,
			final JsonFactory jsonFactory) {
		super(null, httpRequestFactory, jsonFactory);
	}

	public ReadService(final URL baseUrl,
			final HttpRequestFactory httpRequestFactory,
			final JsonFactory jsonFactory) {
		super(baseUrl, null, httpRequestFactory, jsonFactory);
	}

	public ReadService(final String key,
			final HttpRequestFactory httpRequestFactory,
			final JsonFactory jsonFactory) {
		super(key, httpRequestFactory, jsonFactory);
	}

	public ReadService(final URL baseUrl, final String key,
			final HttpRequestFactory httpRequestFactory,
			final JsonFactory jsonFactory) {
		super(baseUrl, key, httpRequestFactory, jsonFactory);
	}

	@SuppressWarnings("unchecked")
	public final Map<String, Object> readRaw(final Query query,
			final Object cursor) throws IOException, FreebaseServiceException {
		final GenericUrl url = new GenericUrl(getBaseUrl() + "/mqlread");
		url.set("query", URLEncoder.encode(query.toJSON(), "UTF-8"));
		url.set("cursor", cursor);

		final String response = fetchPage(url);
		final Map<String, Object> result = (Map<String, Object>) parseJSON(response);
		parseServiceErrors(query, result);
		return result;
	}

	public final Map<String, Object> readRaw(final Query query)
			throws IOException, FreebaseServiceException {
		return readRaw(query, true);
	}

	public final ResultSet read(final Query query) throws IOException {
		return read(query, null);
	}

	public final ResultSet read(final Query query, final String cursor)
			throws IOException {
		return query.buildResultSet(this);
	}

	@SuppressWarnings("unchecked")
	public final void parseServiceErrors(final Query query,
			final Map<String, Object> data) throws FreebaseServiceException {
		if (!data.containsKey("error")) {
			return;
		}

		final Map<String, Object> error = (Map<String, Object>) data
				.get("error");
		final int code = Integer.valueOf((String) error.get("code"));
		final Map<String, Object> errors = (Map<String, Object>) error
				.get("errors");
		throw new FreebaseServiceException(code, (String) errors.get("domain"),
				(String) errors.get("reason"), (String) errors.get("message"),
				data);
	}

}
