package com.narphorium.freebase.results;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.narphorium.freebase.query.DefaultQuery;
import com.narphorium.freebase.query.Query;
import com.narphorium.freebase.services.ReadService;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public abstract class AbstractResultSet implements ResultSet {

	private static final Log LOG = LogFactory.getLog(AbstractResultSet.class);

	private ReadService readService;
	private Query query;
	private List<Result> results = new ArrayList<Result>();
	private int currentResult;
	private Object cursor;
	private int numPages;
	private boolean fetchedFirstPage;

	public AbstractResultSet(final Query query, final ReadService readService) {
		this.readService = readService;
		this.query = new DefaultQuery(query);
		reset();
	}

	public final Query getQuery() {
		return query;
	}

	public final int getNumpages() {
		// TODO: Review the value to an outside caller. Possibly rename it to
		// getNumberOfFetchedPages or document it in that way?
		return numPages;
	}

	public Result current() {
		return currentResult >= 0 && currentResult < results.size() ? results
				.get(currentResult) : null;
	}

	public final void reset() {
		fetchedFirstPage = false;
		numPages = 0;
		cursor = true;
		resetResult();
	}

	private void resetResult() {
		currentResult -= results.size() - 1;
		results.clear();
	}

	public final int size() throws FreebaseServiceException {
		if (!fetchedFirstPage) {
			fetchNextPage();
		}

		return results.size();
	}

	public final boolean isEmpty() {
		return results.isEmpty();
	}

	public final Result next() throws FreebaseServiceException {
		++currentResult;
		if (currentResult >= (results.size() - 1)
				&& ((cursor instanceof Boolean && (Boolean) cursor == true) || (cursor instanceof String))) {
			fetchNextPage();
		}

		return current();
	}

	public final boolean hasNext() throws FreebaseServiceException {
		if (!fetchedFirstPage) {
			fetchNextPage();
		}

		return currentResult < results.size() - 1;
	}

	@SuppressWarnings("unchecked")
	protected void fetchNextPage() throws FreebaseServiceException {
		resetResult();

		try {
			final Map<String, Object> q = readService.readRaw(query, cursor);

			if (q.get("result") instanceof List) {
				final List<Object> r = (List<Object>) q.get("result");
				for (final Object obj : r) {
					results.add(new DefaultResult(query, obj));
				}
			} else {
				final Object obj = q.get("result");
				results.add(new DefaultResult(query, obj));
			}

			final Object c = q.get("cursor");
			if (c != null) {
				cursor = c;
				LOG.info("CURSOR = " + cursor);
			}

			++numPages;
			fetchedFirstPage = true;
		} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public final ReadService getReadService() {
		return readService;
	}

}