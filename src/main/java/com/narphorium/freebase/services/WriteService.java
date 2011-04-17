package com.narphorium.freebase.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;

import com.narphorium.freebase.query.Query;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class WriteService extends AbstractFreebaseService {

	private static final Log LOG = LogFactory.getLog(WriteService.class);

	public WriteService(final HttpClient httpClient) {
		super(httpClient);
	}

	public WriteService(final URL baseUrl, final HttpClient httpClient) {
		super(baseUrl, httpClient);
	}

	public final String write(final Query query)
			throws FreebaseServiceException {
		final List<Query> queries = new ArrayList<Query>();
		queries.add(query);
		return write(queries);
	}

	public final String write(final List<Query> queries)
			throws FreebaseServiceException {
		try {
			final URL url = new URL(getBaseUrl() + "/service/mqlwrite");
			final String envelope = buildWriteQueryEnvelope(queries);
			final Map<String, String> content = new HashMap<String, String>();
			content.put("queries", envelope);
			return postContent(url, content);
		} catch (final MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}

		return null;
	}

	protected final String buildWriteQueryEnvelope(final List<Query> queries) {
		final StringBuilder envelope = new StringBuilder("{");
		final Iterator<Query> i = queries.iterator();
		while (i.hasNext()) {
			final Query query = i.next();
			envelope.append("\"").append(query.getName()).append("\":{");
			envelope.append("\"query\":").append(query.toJSON());
			envelope.append("}");
			if (i.hasNext()) {
				envelope.append(",");
			}
		}

		return envelope.append("}").toString();
	}
}
