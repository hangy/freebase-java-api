package com.narphorium.freebase.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;

import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public abstract class AuthenticationEnabledFreebaseService extends
		AbstractFreebaseService {

	private static final Log LOG = LogFactory.getLog(WriteService.class);

	public AuthenticationEnabledFreebaseService(final HttpClient httpClient) {
		super(httpClient);
	}

	public AuthenticationEnabledFreebaseService(final URL baseUrl,
			final HttpClient httpClient) {
		super(baseUrl, httpClient);
	}

	public final boolean authenticate(final String username,
			final String password) throws FreebaseServiceException {
		try {
			final URL url = new URL(getBaseUrl() + "/account/login");
			final Map<String, String> content = new HashMap<String, String>();
			content.put("username", username);
			content.put("password", password);
			final String result = postContent(url, content);
			return null != result && !result.isEmpty();
		} catch (final MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}

		return false;
	}

	public final boolean logout() {
		try {
			final URL url = new URL(getBaseUrl() + "/account/logout");
			final String result = postContent(url, new HashMap<String, String>(
					0));
			return null != result && !result.isEmpty();
		} catch (final MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}

		return false;
	}
}
