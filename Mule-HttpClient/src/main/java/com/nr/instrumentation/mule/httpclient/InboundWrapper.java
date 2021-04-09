package com.nr.instrumentation.mule.httpclient;

import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;

public class InboundWrapper implements InboundHeaders {
	
	private HttpResponse response = null;
	
	public InboundWrapper(HttpResponse r) {
		response = r;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public String getHeader(String name) {
		if(response != null) {
			return response.getHeaderValue(name);
		}
		return null;
	}

}
