package org.mule.runtime.api.util;

import org.mule.runtime.http.api.domain.message.request.HttpRequest;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;


public class OutboundWrapper implements OutboundHeaders {
	
	private HttpRequest request = null;
	
	public OutboundWrapper(HttpRequest req) {
		request = req;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public void setHeader(String name, String value) {
		MultiMap<String, String> headers = request.getHeaders();
		if(headers != null) {
			headers.put(name, value);
		}
	}

}
