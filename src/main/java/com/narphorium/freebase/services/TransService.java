package com.narphorium.freebase.services;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import org.apache.http.client.HttpClient;

import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class TransService extends AbstractFreebaseService {
	
	public TransService(final HttpClient httpClient) {
		super(httpClient);
	}
	
	public TransService(final URL baseUrl, final HttpClient httpClient) {
		super(baseUrl, httpClient);
	}
	
	public Image fetchImage(final String guid) throws FreebaseServiceException {
		return null;
	}
	
	public String fetchArticle(String id) throws IOException, FreebaseServiceException {
		if (id.startsWith("#")) {
			id = "/guid/" + id.substring(1);
		}
		String url = getBaseUrl() + "/trans/raw" + id;
		String content = fetchPage(url);
		return content;
	}

}
