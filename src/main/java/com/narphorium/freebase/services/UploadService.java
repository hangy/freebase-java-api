package com.narphorium.freebase.services;

import java.net.URL;

import org.apache.http.client.HttpClient;

import com.narphorium.freebase.auth.Authorizer;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class UploadService extends AbstractFreebaseService {

	public UploadService(final Authorizer authorizer,
			final HttpClient httpClient) {
		super(authorizer, httpClient);
	}

	public UploadService(final URL baseUrl, final Authorizer authorizer,
			final HttpClient httpClient) {
		super(baseUrl, authorizer, httpClient);
	}

	// TODO
	public final void upload(final byte[] content)
			throws FreebaseServiceException {

	}

}
