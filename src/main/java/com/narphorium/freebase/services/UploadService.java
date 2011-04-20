package com.narphorium.freebase.services;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;

import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class UploadService extends AbstractFreebaseService {
	private static final Log LOG = LogFactory.getLog(UploadService.class);

	public UploadService(final HttpClient httpClient) {
		super(httpClient);
	}

	public UploadService(final URL baseUrl, final HttpClient httpClient) {
		super(baseUrl, httpClient);
	}

	public final void upload(final byte[] content, final String contentType)
			throws FreebaseServiceException {
		try {
			final URL url = new URL(getBaseUrl() + "/reconciliation/upload");
			final String result = uploadFile(url, content, contentType);
			LOG.debug("Upload response: " + result);
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}
	}

}
