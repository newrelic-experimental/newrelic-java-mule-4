package com.nr.instrumentation.mule.httpconnector;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.mule.sdk.api.http.domain.message.request.HttpRequest;

import com.newrelic.api.agent.ExtendedRequest;
import com.newrelic.api.agent.HeaderType;

public class SdkRequestWrapper extends ExtendedRequest {

	private final HttpRequest request;

	public SdkRequestWrapper(HttpRequest request) {
		this.request = request;
	}

	@Override
	public String getRequestURI() {
		return request.getPath();
	}

	@Override
	public String getRemoteUser() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration getParameterNames() {
		return Collections.enumeration(request.getQueryParams().keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		List<String> values = request.getQueryParams().getAll(name);
		return values.toArray(new String[0]);
	}

	@Override
	public Object getAttribute(String name) {
		return null;
	}

	@Override
	public String getCookieValue(String name) {
		return null;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public String getHeader(String name) {
		return request.getHeaderValue(name);
	}

	@Override
	public String getMethod() {
		return request.getMethod();
	}

}
