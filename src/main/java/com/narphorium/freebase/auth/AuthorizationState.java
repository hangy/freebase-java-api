package com.narphorium.freebase.auth;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.util.DateTime;

public class AuthorizationState {

	private static final int MILLISECONDS_TO_SECONDS = 1000;

	private String accessToken;

	private DateTime expirationDate;

	private String refreshToken;

	public AuthorizationState(final AccessTokenResponse accessToken) {
		apply(accessToken);
	}

	public final void apply(final AccessTokenResponse accessToken) {
		this.accessToken = accessToken.accessToken;
		this.expirationDate = new DateTime(getCurrentUnixTimestamp()
				+ accessToken.expiresIn);
		this.refreshToken = accessToken.refreshToken;
	}

	public final String getAccessToken() {
		return accessToken;
	}

	public final boolean hasExpired() {
		final long currentTimestamp = getCurrentUnixTimestamp();
		return currentTimestamp >= expirationDate.value;
	}

	public final String getRefreshToken() {
		return refreshToken;
	}

	private static long getCurrentUnixTimestamp() {
		return System.currentTimeMillis() / MILLISECONDS_TO_SECONDS;
	}

}