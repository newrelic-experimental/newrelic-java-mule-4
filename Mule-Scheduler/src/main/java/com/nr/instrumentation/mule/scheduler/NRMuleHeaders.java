package com.nr.instrumentation.mule.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;

public class NRMuleHeaders implements Headers {

	private HashMap<String, String> headers = new HashMap<String, String>();

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public String getHeader(String name) {
		return headers.get(name);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		String value = headers.get(name);
		List<String> list = new ArrayList<String>();
		if(value != null) {
			list.add(value);
		}
		return list;
	}

	@Override
	public void setHeader(String name, String value) {
		headers.put(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}

	@Override
	public boolean containsHeader(String name) {
		return headers.containsKey(name);
	}

	public boolean isEmpty() {
		return headers.isEmpty();
	}
	
	public void clear() {
		headers.clear();
	}

}
