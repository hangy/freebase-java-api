package com.narphorium.freebase.services;

import java.net.URL;

import com.google.api.client.http.HttpRequestFactory;
import com.narphorium.freebase.results.SearchResultSet;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class SearchService extends AbstractFreebaseService {

	public SearchService(final String key, final HttpRequestFactory httpRequestFactory) {
		super(key, httpRequestFactory);
	}

	public SearchService(final URL baseUrl, final String key,
			final HttpRequestFactory httpRequestFactory) {
		super(baseUrl, key, httpRequestFactory);
	}

	// TODO
	public final SearchResultSet search(final String query)
			throws FreebaseServiceException {
		return null;
	}

}
