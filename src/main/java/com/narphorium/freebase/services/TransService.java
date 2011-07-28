package com.narphorium.freebase.services;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class TransService extends AbstractFreebaseService {

	private static final Log LOG = LogFactory.getLog(TransService.class);

	public TransService(final String key,
			final HttpRequestFactory httpRequestFactory) {
		super(key, httpRequestFactory);
	}

	public TransService(final URL baseUrl, final String key,
			final HttpRequestFactory httpRequestFactory) {
		super(baseUrl, key, httpRequestFactory);
	}

	public final Image fetchImage(final String id)
			throws FreebaseServiceException {
		try {
			final String url = getUrlForId(id, "image");
			final HttpRequest request = buildGetRequest(url);
			final HttpResponse response = request.execute();
			if (!response.isSuccessStatusCode) {
				throw new IOException(response + ": " + response.statusCode
						+ " " + response.statusMessage);
			}

			return ImageIO.read(response.getContent());
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	public final String fetchArticle(final String id) throws IOException,
			FreebaseServiceException {
		final String url = getUrlForId(id, "text");
		return fetchPage(url);
	}

	private String getUrlForId(final String id, final String type) {
		String url = getBaseUrl() + "/" + type;

		if (id.startsWith("#")) {
			url += "/guid/" + id.substring(1);
		} else {
			url += id;
		}

		return url;
	}

}
