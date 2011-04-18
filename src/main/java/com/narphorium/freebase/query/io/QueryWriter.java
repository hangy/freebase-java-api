package com.narphorium.freebase.query.io;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.narphorium.freebase.query.Query;

public class QueryWriter {

	private String indent;
	private String newline;

	public QueryWriter(final String indent, final boolean multiline) {
		this.indent = indent;
		this.newline = multiline ? "\n" : "";
	}

	public final String write(final Query query) {
		return writeNode(query.getData(), "", false);
	}

	@SuppressWarnings("unchecked")
	private String writeNode(final Object root, final String offset,
			final boolean withholdStartOffset) {
		if (root instanceof List) {
			final StringBuilder result = new StringBuilder(
					withholdStartOffset ? "" : offset).append("[").append(
					newline);
			final List<Object> list = (List<Object>) root;
			for (Iterator<Object> i = list.iterator(); i.hasNext();) {
				result.append(writeNode(i.next(), offset + indent, false));
				if (i.hasNext()) {
					result.append(",");
				}

				result.append(newline);
			}

			return result.append("]").toString();
		} else if (root instanceof Map) {
			final StringBuilder result = new StringBuilder(
					withholdStartOffset ? "" : offset).append("{").append(
					newline);
			Map<String, Object> map = (Map<String, Object>) root;
			for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
				final String key = i.next();
				result.append(offset).append(indent).append("\"").append(key)
						.append("\" : ")
						.append(writeNode(map.get(key), offset + indent, true));
				if (i.hasNext()) {
					result.append(",");
				}

				result.append(newline);
			}

			return result.append(offset).append("}").toString();
		} else if (root == null) {
			return "null";
		} else if (root instanceof String) {
			final String value = root.toString();
			return "\"" + value + "\"";
		} else if (objectIsNumber(root) || root instanceof Boolean) {
			return root.toString();
		}

		return null;
	}

	private boolean objectIsNumber(Object obj) {
		return obj instanceof Integer || obj instanceof Long
				|| obj instanceof Float || obj instanceof Double;
	}

}
