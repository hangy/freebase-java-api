package com.narphorium.freebase.query;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.narphorium.freebase.results.DefaultResultSet;
import com.narphorium.freebase.results.ResultSet;
import com.narphorium.freebase.services.ReadService;

public class DefaultQuery extends AbstractQuery implements Query {

	private static final Log LOG = LogFactory.getLog(DefaultQuery.class);

	public DefaultQuery(final Object data, final List<Parameter> parameters,
			final List<Parameter> blankFields) {
		super(data, parameters, blankFields);
	}

	public DefaultQuery(final Query query) {
		super(query);
	}

	public final void parseParameterValue(final String name,
			final String rawValue) {
		final Parameter parameter = getParameter(name);
		if (parameter == null) {
			LOG.error("Parameter \"" + name + "\" does not exist.");
			return;
		}

		Object value = null;
		if (rawValue.length() > 0) {
			value = parameter.parseValue(rawValue);
		}

		setParameterValue(name, value);
	}

	public final ResultSet buildResultSet(final ReadService readService) {
		return new DefaultResultSet(this, readService);
	}

}
