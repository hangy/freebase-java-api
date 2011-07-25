package com.narphorium.freebase.services;

import java.net.URL;

import com.google.api.client.http.HttpTransport;
import com.narphorium.freebase.results.SearchResultSet;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class SearchService extends AbstractFreebaseService {

	public SearchService(final String key, final HttpTransport httpTransport) {
		super(key, httpTransport);
	}

	public SearchService(final URL baseUrl, final String key,
			final HttpTransport httpTransport) {
		super(baseUrl, key, httpTransport);
	}

	// TODO
	public final SearchResultSet search(final String query)
			throws FreebaseServiceException {
		return null;
	}

}
