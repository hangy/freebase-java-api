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
import org.stringtree.json.JSONReader;

import com.narphorium.freebase.query.DefaultQuery;
import com.narphorium.freebase.query.JsonPath;
import com.narphorium.freebase.query.Parameter;
import com.narphorium.freebase.query.Query;

public class QueryParser {
	
	private static final Log LOG = LogFactory.getLog(QueryParser.class);
	private static Pattern parameterNamePattern = Pattern.compile("([\\d\\w_]+):([\\d\\w_\\/]+)");
	private static Matcher parameterNameMatcher = parameterNamePattern.matcher("");
	
	public Query parse(String name, String queryString) {
		JSONReader reader = new JSONReader();
		List<Parameter> parameters = new ArrayList<Parameter>();
		Map<String, Parameter> parametersByName = new HashMap<String, Parameter>();
		List<Parameter> blankFields = new ArrayList<Parameter>();
		Object data = reader.read(queryString);		
		processData(new JsonPath(), data, blankFields, parameters, parametersByName, true);
		return new DefaultQuery(name, data, parameters, blankFields);
	}
	
	public Query parse(File queryFile) {
		String name = queryFile.getName().substring(0, queryFile.getName().lastIndexOf('.'));
		StringBuffer queryString = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(queryFile));
			try {
				String line = null;
				while ((line = reader.readLine()) != null) {
					queryString.append(line);
					queryString.append("\n");
				}
				
				return parse(name, queryString.toString());
			}
			finally {
				reader.close();
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private void processData(JsonPath path, Object data, List<Parameter> blankFields, List<Parameter> parameters, Map<String, Parameter> parametersByName, boolean isRoot) {
		if (data == null) {
			LOG.info("data is null");
		} else if (data instanceof List) {
			int i = 0;
			for (Object element : (List<Object>)data) {
				JsonPath childPath = new JsonPath(path);
				if (!isRoot) {
					childPath.addElement(i);
				}
				processData(childPath, element, blankFields, parameters, parametersByName, false);
			}
		} else if (data instanceof Map) {
			Map<String, Object> mapData = (Map<String, Object>)data;
			for (String key : mapData.keySet()){
				Object value = mapData.get(key);
				JsonPath childPath = new JsonPath(path);
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
					if (value instanceof Map && 
						((Map<String, Object>)value).containsKey("value"))
					{
						childPath.addElement("value");
					}
					//parameter.addPath(childPath);
					parameter.setPath(childPath);
				}
				if (value == null) {
					Parameter blankField = new Parameter(name, id, value);
					blankField.setPath(childPath);
					blankFields.add(blankField);
				} else {
					processData(childPath, value, blankFields, parameters, parametersByName, false);
				}
			}
		}
	}

}
