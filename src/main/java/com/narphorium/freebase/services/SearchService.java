package com.narphorium.freebase.services;

import java.net.URL;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpRequestFactory;
import com.narphorium.freebase.results.SearchResultSet;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class SearchService extends AbstractFreebaseService {

	public SearchService(final HttpRequestFactory httpRequestFactory,
			final JsonFactory jsonFactory) {
		super(null, httpRequestFactory, jsonFactory);
	}

	public SearchService(final URL baseUrl,
			final HttpRequestFactory httpRequestFactory,
			final JsonFactory jsonFactory) {
		super(baseUrl, null, httpRequestFactory, jsonFactory);
	}

	public SearchService(final String key,
			final HttpRequestFactory httpRequestFactory,
			final JsonFactory jsonFactory) {
		super(key, httpRequestFactory, jsonFactory);
	}

	public SearchService(final URL baseUrl, final String key,
			final HttpRequestFactory httpRequestFactory,
			final JsonFactory jsonFactory) {
		super(baseUrl, key, httpRequestFactory, jsonFactory);
	}

	// TODO
	public final SearchResultSet search(final String query)
			throws FreebaseServiceException {
		throw new UnsupportedOperationException(
				"This method is currently unsupported.");
	}

}