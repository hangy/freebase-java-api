package com.narphorium.freebase.results;

import com.google.api.client.json.JsonFactory;
import com.narphorium.freebase.query.Query;
import com.narphorium.freebase.services.ReadService;

public class DefaultResultSet extends AbstractResultSet {

	public DefaultResultSet(final JsonFactory jsonFactory, final Query query,
			final ReadService readService) {
		super(jsonFactory, query, readService);
	}

}
