package com.narphorium.freebase.auth;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.api.client.auth.oauth2.AccessTokenErrorResponse;
import com.google.api.client.auth.oauth2.AccessTokenRequest;
import com.google.api.client.auth.oauth2.AccessTokenResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

public class Authorizer {

	private static final String BASE_AUTHORIZATION_URL = "https://accounts.google.com/o/oauth2/token";

	private static final Log LOG = LogFactory.getLog(Authorizer.class);

	private final String clientId;

	private final String clientSecret;

	private AuthorizationState state;

	public Authorizer(final String clientId, final String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	public final boolean authorize(final String code) throws IOException {
		try {
			final AccessTokenRequest.AuthorizationCodeGrant request = new AccessTokenRequest.AuthorizationCodeGrant();
			setupRequest(request);
			request.code = code;
			request.redirectUri = "urn:ietf:wg:oauth:2.0:oob";

			final AccessTokenResponse response = request.execute().parseAs(
					AccessTokenResponse.class);
			LOG.debug("Access token: " + response.accessToken);
			state = new AuthorizationState(response);
			return true;
		} catch (HttpResponseException e) {
			final AccessTokenErrorResponse response = e.response
					.parseAs(AccessTokenErrorResponse.class);
			LOG.error(response.error, e);
			state = null;
			return false;
		}
	}

	public final boolean refresh() throws IOException {
		try {
			final AccessTokenRequest.RefreshTokenGrant request = new AccessTokenRequest.RefreshTokenGrant();
			setupRequest(request);
			request.refreshToken = this.state.getRefreshToken();

			final AccessTokenResponse response = request.execute().parseAs(
					AccessTokenResponse.class);
			LOG.debug("Access token: " + response.accessToken);
			state.apply(response);
			return true;
		} catch (HttpResponseException e) {
			final AccessTokenErrorResponse response = e.response
					.parseAs(AccessTokenErrorResponse.class);
			LOG.error(response.error, e);
			state = null;
			return false;
		}
	}
	
	private void setupRequest(final AccessTokenRequest request) {
		request.transport = new NetHttpTransport();
		request.jsonFactory = new JacksonFactory();
		request.useBasicAuthorization = false;
		request.authorizationServerUrl = BASE_AUTHORIZATION_URL;
		request.clientId = clientId;
		request.clientSecret = clientSecret;
	}

}