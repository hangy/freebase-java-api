package com.narphorium.freebase.services.exceptions;

import java.util.Map;

public class FreebaseServiceException extends Exception {

	private static final long serialVersionUID = -8328410897564469411L;

	private final int code;
	private final String domain;
	private final String reason;
	private final Map<String, Object> data;

	public FreebaseServiceException(final int code, final String domain,
			final String reason, final String message,
			final Map<String, Object> data) {
		super(message);
		this.code = code;
		this.domain = domain;
		this.reason = reason;
		this.data = data;
	}

	public final int getCode() {
		return code;
	}

	public final String getDomain() {
		return domain;
	}

	public final String getReason() {
		return reason;
	}

	public final Map<String, Object> getData() {
		return data;
	}

}
