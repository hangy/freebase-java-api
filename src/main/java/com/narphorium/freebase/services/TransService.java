package com.narphorium.freebase.services;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class TransService extends AbstractFreebaseService {
	
	public TransService() {
		super();
	}
	
	public TransService(final URL baseUrl) {
		super(baseUrl);
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
