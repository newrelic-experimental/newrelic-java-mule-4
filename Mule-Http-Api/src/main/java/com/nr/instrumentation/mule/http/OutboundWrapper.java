package com.nr.instrumentation.mule.http;

import java.util.Collection;

import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.request.HttpRequestBuilder;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;

public class OutboundWrapper implements OutboundHeaders {

	private HttpRequestBuilder builder = null;
	
	public OutboundWrapper(HttpRequest req) {
		MultiMap<String, String> headers = new MultiMap<String, String>();
		Collection<String> headerNames = req.getHeaderNames();
		for(String headerName : headerNames) {
			Collection<String> values = req.getHeaderValues(headerName);
			headers.put(headerName, values);
		}
		
		builder = HttpRequest.builder().method(req.getMethod())
				.queryParams(req.getQueryParams())
				.uri(req.getUri())
				.headers(headers);
		
	}
	
	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public void setHeader(String name, String value) {
		builder.addHeader(name, value);
		
	}
	
	public HttpRequest getRequest() {
		
		
		return builder.build();
	}

}
