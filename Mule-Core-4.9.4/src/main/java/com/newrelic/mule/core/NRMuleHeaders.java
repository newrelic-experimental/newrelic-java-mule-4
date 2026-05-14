package com.newrelic.mule.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;

public class NRMuleHeaders implements Headers {

	private HashMap<String, String> headerMap = new HashMap<String, String>();

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public String getHeader(String name) {
		return headerMap.get(name);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		String value = headerMap.get(name);
		List<String> list = new ArrayList<String>();
		if(value != null) {
			list.add(value);
		}
		return list;
	}

	@Override
	public void setHeader(String name, String value) {
		headerMap.put(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		headerMap.put(name, value);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return headerMap.keySet();
	}

	@Override
	public boolean containsHeader(String name) {
		return headerMap.containsKey(name);
	}

	public boolean isEmpty() {
		return headerMap.isEmpty();
	}
	
	public void clear() {
		headerMap.clear();
	}

}
