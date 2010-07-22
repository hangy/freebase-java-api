package com.narphorium.freebase.query;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.narphorium.freebase.results.DefaultResultSet;
import com.narphorium.freebase.results.ResultSet;
import com.narphorium.freebase.services.ReadService;

public class DefaultQuery extends AbstractQuery implements Query {

	private static final Log LOG = LogFactory.getLog(DefaultQuery.class);

	public DefaultQuery(String name, Object data, List<Parameter> parameters,
			List<Parameter> blankFields) {
		super(name, data, parameters, blankFields);

		/*
		 * for (Parameter parameter : parameters) {
		 * System.out.print(parameter.getName()); for (Object[] path :
		 * parameter.getPaths()) { System.out.print(" - "); for (Object e :
		 * path) { System.out.print("/" + e); } System.out.println(); } }
		 */
	}

	public DefaultQuery(Query query) {
		super(query);
	}

	public void parseParameterValue(String name, String rawValue) {
		Parameter parameter = parametersByName.get(name);
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

	public ResultSet buildResultSet(ReadService readService) {
		return new DefaultResultSet(this, readService);
	}

}
