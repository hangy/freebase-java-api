package com.narphorium.freebase.services.exceptions;

public class FreebaseServiceException extends Exception {

	private static final long serialVersionUID = -3079674776812838795L;
	
	private String code;
	private String host;
	private int port;
	private double timeout;
	
	public FreebaseServiceException(final String code, final String message, final String host, final int port, final double timeout) {
		super(message);
		this.code = code;
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}

	@SuppressWarnings("unused")
	private void setCode(final String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	@SuppressWarnings("unused")
	private void setHost(final String host) {
		this.host = host;
	}
	
	public String getHost() {
		return host;
	}

	@SuppressWarnings("unused")
	private void setPort(final int port) {
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}

	@SuppressWarnings("unused")
	private void setTimeout(final double timeout) {
		this.timeout = timeout;
	}
	
	public double getTimeout() {
		return timeout;
	}

}
