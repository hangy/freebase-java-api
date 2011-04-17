package com.narphorium.freebase.results;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.narphorium.freebase.query.DefaultQuery;
import com.narphorium.freebase.query.JsonPath;
import com.narphorium.freebase.query.Parameter;
import com.narphorium.freebase.query.Query;

public class DefaultResult implements Result {

	private static final Log LOG = LogFactory.getLog(DefaultResult.class);
	private static final String DATEFORMAT = "yyyy-mm-dd";

	private Query query;
	private Object jsonData;

	public DefaultResult(final Query query, final Object jsonData) {
		this.query = new DefaultQuery(query);
		this.jsonData = jsonData;
	}

	public Object getObject(final JsonPath path) {
		return path.getValue(jsonData);
	}

	public Object getObject(final String variable) {
		final Parameter parameter = query.getParameter(variable);
		if (parameter == null) {
			LOG.error("Parameter \"" + variable + "\" does not exist.");
			return null;
		}

		return getObject(parameter.getPath());
	}

	@SuppressWarnings("unchecked")
	public List<Object> getCollection(final String variable) {
		return (List<Object>) getObject(variable);
	}

	public boolean getBoolean(final String variable) {
		return (Boolean) getObject(variable);
	}

	public Date getDate(final String variable) {
		final DateFormat dateFormat = getDateFormat();
		try {
			return dateFormat.parse(getString(variable));
		} catch (ParseException e) {
			LOG.debug(e.getMessage(), e);
		}
		return null;
	}

	public float getFloat(final String variable) {
		return (Float) getObject(variable);
	}

	public int getInteger(final String variable) {
		return (Integer) getObject(variable);
	}

	public String getString(final String variable) {
		return (String) getObject(variable);
	}

	public Query getQuery() {
		return query;
	}

	private static DateFormat getDateFormat() {
		return new SimpleDateFormat(DATEFORMAT, Locale.ROOT);
	}

}
