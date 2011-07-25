package com.narphorium.freebase.services;

import java.net.URL;

import org.apache.http.client.HttpClient;

import com.google.api.client.json.JsonFactory;
import com.narphorium.freebase.results.SearchResultSet;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class SearchService extends AbstractFreebaseService {

	public SearchService(final JsonFactory jsonFactory, final String key,
			final HttpClient httpClient) {
		super(jsonFactory, key, httpClient);
	}

	public SearchService(final JsonFactory jsonFactory, final URL baseUrl,
			final String key, final HttpClient httpClient) {
		super(jsonFactory, baseUrl, key, httpClient);
	}

	// TODO
	public final SearchResultSet search(final String query)
			throws FreebaseServiceException {
		return null;
	}

}
