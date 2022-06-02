package com.nr.instrumentation.mule.http;

import java.net.URI;
import java.util.function.BiConsumer;

import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;

public class NRBiConsumer implements BiConsumer<HttpResponse, Throwable> {
	
	Segment segment = null;
	URI uri = null;
	String proced = null;
	
	public NRBiConsumer(String n,URI u, String proc) {
		segment = NewRelic.getAgent().getTransaction().startSegment(n);
		uri = u;
		proced = proc;
	}

	@Override
	public void accept(HttpResponse response, Throwable u) {
		HttpParameters params = null;
		if(response != null) {
			InboundWrapper wrapper = new InboundWrapper(response);
			params = HttpParameters.library("Mule-Http").uri(uri).procedure(proced).inboundHeaders(wrapper).build();
		} else {
			if(u != null) {
				NewRelic.noticeError(u);
			}
			params = HttpParameters.library("Mule-Http").uri(uri).procedure(proced).noInboundHeaders().build();
		}
		if(segment != null) {
			if(params != null) {
				segment.reportAsExternal(params);
			}
			segment.end();
		}
	}

}
