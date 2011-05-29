package com.narphorium.freebase.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;

import com.narphorium.freebase.auth.Authorizer;
import com.narphorium.freebase.query.Query;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class WriteService extends AbstractFreebaseService {

	private static final Log LOG = LogFactory.getLog(WriteService.class);

	public WriteService(final Authorizer authorizer, final HttpClient httpClient) {
		super(authorizer, httpClient);
	}

	public WriteService(final URL baseUrl, final Authorizer authorizer,
			final HttpClient httpClient) {
		super(baseUrl, authorizer, httpClient);
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
