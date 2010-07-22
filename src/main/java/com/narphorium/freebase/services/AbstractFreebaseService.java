package com.narphorium.freebase.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.stringtree.json.JSONReader;
import org.stringtree.json.JSONWriter;

public class AbstractFreebaseService {
	
	private static final Log LOG = LogFactory.getLog(AbstractFreebaseService.class);
	private static final String USER_AGENT = "Freebase Java API (" + System.getProperty("os.name") + ")";
	
	private MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	private HttpClient httpClient = new HttpClient(connectionManager);
	
	private URL baseUrl;
	private JSONReader jsonParser = new JSONReader();
	private JSONWriter jsonWriter = new JSONWriter();
	
	public AbstractFreebaseService() {
		try {
			baseUrl = new URL("http://www.freebase.com/api");
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public AbstractFreebaseService(URL baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public synchronized URL getBaseUrl() { 
		return baseUrl;
	}

	protected String fetchPage(String url) throws IOException {
		StringBuffer content = new StringBuffer();
		url = url.replaceAll(" ", "%20");
		
		LOG.debug("URL: " + url);
		
		GetMethod method = new GetMethod(url);
		try {
			method.setRequestHeader("User-Agent", USER_AGENT);
			
			int status = httpClient.executeMethod(method);
			
			if (status != HttpStatus.SC_OK) {
	        	throw new IOException(status + ": Unable to reach host.");
	        }
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), "utf8"));
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	        	content.append(line + "\n");
	        }
	        reader.close();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			method.releaseConnection();
		}
		
		LOG.debug(content);
		
		return content.toString();
	}
	
	protected String postContent(URL url, Map<String, String> content) throws IOException {
		StringBuffer result = new StringBuffer();
		
		PostMethod method = new PostMethod(url.toString());
		method.setRequestHeader("User-Agent", USER_AGENT);
		method.setRequestHeader("X-Metaweb-Request", "");
		for (String parameter : content.keySet()) {
			method.setParameter(parameter, content.get(parameter));
		}
		
		try {
			int status = httpClient.executeMethod(method);
			
			if (status != HttpStatus.SC_OK) {
	        	throw new IOException(status + ": Unable to reach host.");
	        }
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), "utf8"));
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	        	result.append(line + "\n");
	        }
	        reader.close();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			method.releaseConnection();
		}
		
		return result.toString();
	}
	
	protected Object parseJSON(String results) throws IOException {
		return jsonParser.read(results);
	}
	
	protected String generateJSON(Object object) {
		return jsonWriter.write(object);
	}
}
