package com.narphorium.freebase.services;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;

import com.narphorium.freebase.auth.Authorizer;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class UploadService extends AbstractFreebaseService {
	private static final Log LOG = LogFactory.getLog(UploadService.class);

	public UploadService(final String key, final Authorizer authorizer,
			final HttpClient httpClient) {
		super(key, authorizer, httpClient);
	}

	public UploadService(final URL baseUrl, final String key,
			final Authorizer authorizer, final HttpClient httpClient) {
		super(baseUrl, key, authorizer, httpClient);
	}

	public final void uploadImage(final byte[] content, final String contentType)
			throws FreebaseServiceException {
		this.upload(content, contentType, "image");
	}

	public final void uploadText(final byte[] content, final String contentType)
			throws FreebaseServiceException {
		this.upload(content, contentType, "text");
	}

	private final void upload(final byte[] content, final String contentType,
			final String uploadType) throws FreebaseServiceException {
		try {
			final URL url = new URL(getBaseUrl() + "/" + uploadType);
			final String result = uploadFile(url, content, contentType);
			LOG.debug("Upload response: " + result);
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
