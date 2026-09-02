package com.nr.instrumentation.mule.extensions;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;

import org.mule.runtime.api.util.MultiMap;

import com.newrelic.api.agent.ExtendedRequest;
import com.newrelic.api.agent.HeaderType;

/**
 * Wraps a connector-specific attributes object (e.g. HttpRequestAttributes) purely via
 * reflection, so this class carries no compile-time or class-loading dependency on any
 * connector's own types. Safe to reference from a generic, connector-agnostic dispatch
 * point that runs regardless of which connector is in use.
 */
public class ReflectiveHttpRequestWrapper extends ExtendedRequest {

	private final Object attributes;

	public ReflectiveHttpRequestWrapper(Object attributes) {
		this.attributes = attributes;
	}

	private Object invoke(String methodName) {
		try {
			Method m = attributes.getClass().getMethod(methodName);
			return m.invoke(attributes);
		} catch (Throwable t) {
			return null;
		}
	}

	@Override
	public String getRequestURI() {
		Object uri = invoke("getRequestUri");
		return uri != null ? uri.toString() : null;
	}

	@Override
	public String getRemoteUser() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration getParameterNames() {
		return Collections.emptyEnumeration();
	}

	@Override
	public String[] getParameterValues(String name) {
		return null;
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

	@SuppressWarnings("unchecked")
	@Override
	public String getHeader(String name) {
		Object headersObj = invoke("getHeaders");
		if (headersObj instanceof MultiMap) {
			return ((MultiMap<String, String>) headersObj).get(name);
		}
		return null;
	}

	@Override
	public String getMethod() {
		Object method = invoke("getMethod");
		return method != null ? method.toString() : null;
	}

}
