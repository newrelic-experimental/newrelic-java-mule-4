package com.nr.instrumentation.mule.http;

import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;

public class InboundWrapper implements InboundHeaders {
	
	HttpResponse response = null;
	
	public InboundWrapper(HttpResponse r) {
		response = r;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public String getHeader(String name) {
		return response.getHeaderValue(name);
	}

}
