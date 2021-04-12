package com.nr.instrumentation.mule.soap;

import java.util.Optional;

import org.mule.runtime.extension.api.soap.message.DispatchingRequest;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;

public class InboundWrapper implements InboundHeaders {
	
	private DispatchingRequest request = null;

	public InboundWrapper(DispatchingRequest req) {
		request = req;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public String getHeader(String name) {
		Optional<String> value = request.getHeader(name);
		if(value.isPresent()) {
			return value.get();
		}
		return null;
	}

}
