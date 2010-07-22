package com.narphorium.freebase.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.narphorium.freebase.query.Query;
import com.narphorium.freebase.results.AbstractResultSet;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class WriteService extends AbstractFreebaseService {
	
	private static final Log LOG = LogFactory.getLog(AbstractResultSet.class);
	
	public WriteService() {
		super();
	}
	
	public WriteService(final URL baseUrl) {
		super(baseUrl);
	}
	
	public boolean authenticate(String username, String password) throws FreebaseServiceException {
		try {
			URL url = new URL(getBaseUrl() + "/account/login");
			Map<String, String> content = new HashMap<String, String>();
			content.put("username", username);
			content.put("password", password);
			postContent(url, content);			
			return true;
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}
	
	public String write(final Query query) throws FreebaseServiceException {
		List<Query> queries = new ArrayList<Query>();
		queries.add(query);
		return write(queries);
	}

	public String write(final List<Query> queries) throws FreebaseServiceException {
		try {
			URL url = new URL(getBaseUrl() + "/service/mqlwrite");
			String envelope = buildWriteQueryEnvelope(queries);
			Map<String, String> content = new HashMap<String, String>();
			content.put("queries", envelope);
			return postContent(url, content);
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	protected String buildWriteQueryEnvelope(final List<Query> queries) {
		String envelope = "{";
		Iterator<Query> i = queries.iterator();
		while (i.hasNext()) {
			Query query = i.next();
			envelope += "\"" + query.getName() + "\":{";
			envelope += "\"query\":" + query.toJSON();
			envelope += "}";
			if (i.hasNext()) {
				envelope += ",";
			}
		}
		envelope += "}";
		return envelope;
	}
}
