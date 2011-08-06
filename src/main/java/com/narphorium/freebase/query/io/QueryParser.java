package com.narphorium.freebase.query.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.narphorium.freebase.query.DefaultQuery;
import com.narphorium.freebase.query.JsonPath;
import com.narphorium.freebase.query.Parameter;
import com.narphorium.freebase.query.Query;

public class QueryParser {

	private static final Log LOG = LogFactory.getLog(QueryParser.class);
	private static Pattern parameterNamePattern = Pattern
			.compile("([\\d\\w_]+):([\\d\\w_\\/]+)(?:>|<|<=|>=|~=|\\|=)?");
	private static Matcher parameterNameMatcher = parameterNamePattern
			.matcher("");

	private final JsonFactory jsonFactory;

	public QueryParser(final JsonFactory jsonFactory) {
		if (null == jsonFactory) {
			throw new IllegalArgumentException("jsonFactory cannot be null");
		}

		this.jsonFactory = jsonFactory;
	}

	public final Query parse(final String queryString) {
		final List<Parameter> parameters = new ArrayList<Parameter>();
		final Map<String, Parameter> parametersByName = new HashMap<String, Parameter>();
		final List<Parameter> blankFields = new ArrayList<Parameter>();
		Object data;
		try {
			data = queryIsArray(queryString) ? jsonFactory.fromString(
					queryString, GenericJson[].class) : jsonFactory.fromString(
					queryString, GenericJson.class);
		} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
			data = null;
		}

		processData(new JsonPath(), data, blankFields, parameters,
				parametersByName, true);
		return new DefaultQuery(jsonFactory, data, parameters, blankFields);
	}

	public final Query parse(final File queryFile) {
		final StringBuilder queryString = new StringBuilder();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(
					queryFile));
			try {
				String line = null;
				while ((line = reader.readLine()) != null) {
					queryString.append(line);
					queryString.append("\n");
				}

				return parse(queryString.toString());
			} finally {
				reader.close();
			}
		} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
		}

		return null;
	}

	private boolean queryIsArray(final String queryString) {
		return null != queryString && queryString.trim().startsWith("[");
	}

	@SuppressWarnings("unchecked")
	private void processData(final JsonPath path, final Object data,
			final List<Parameter> blankFields,
			final List<Parameter> parameters,
			final Map<String, Parameter> parametersByName, final boolean isRoot) {
		if (data == null) {
			LOG.info("data is null");
		} else if (data.getClass().isArray()) {
			int i = 0;
			for (final Object element : (Object[]) data) {
				final JsonPath childPath = new JsonPath(path);
				if (!isRoot) {
					childPath.addElement(i);
				}

				processData(childPath, element, blankFields, parameters,
						parametersByName, false);
			}
		} else if (data instanceof Map) {
			Map<String, Object> mapData = (Map<String, Object>) data;
			for (final String key : mapData.keySet()) {
				final Object value = mapData.get(key);
				final JsonPath childPath = new JsonPath(path);
				childPath.addElement(key);
				String id = key;
				String name = null;
				parameterNameMatcher.reset(key);
				if (parameterNameMatcher.matches()) {
					name = parameterNameMatcher.group(1);
					id = parameterNameMatcher.group(2);
					Parameter parameter = parametersByName.get(name);

					if (parameter == null) {
						parameter = new Parameter(name, id, value);
						parametersByName.put(name, parameter);
						parameters.add(parameter);
					}

					if (value instanceof Map
							&& ((Map<String, Object>) value)
									.containsKey("value")) {
						childPath.addElement("value");
					}

					parameter.setPath(childPath);
				}

				if (value == null) {
					Parameter blankField = new Parameter(name, id, value);
					blankField.setPath(childPath);
					blankFields.add(blankField);
				} else {
					processData(childPath, value, blankFields, parameters,
							parametersByName, false);
				}
			}
		}
	}

}
