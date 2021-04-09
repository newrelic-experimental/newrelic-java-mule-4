package com.nr.instrumentation.mule.httpclient;

import java.util.function.BiConsumer;

import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;

public class NRBiConsumer implements BiConsumer<HttpResponse, Throwable> {
	
	private Segment segment = null;
	private HttpParameters params = null;
	
	public NRBiConsumer(HttpParameters p) {
		segment = NewRelic.getAgent().getTransaction().startSegment("HttpClient");
		params = p;
	}

	@Override
	public void accept(HttpResponse t, Throwable u) {
		InboundWrapper wrapper = null;
		if(u != null) {
			NewRelic.noticeError(u);
		} else if(t != null) {
			 wrapper = new InboundWrapper(t);
		}
		HttpParameters p = null;
		if(wrapper != null) {
			p = HttpParameters.library(params.getLibrary()).uri(params.getUri()).procedure(params.getProcedure()).inboundHeaders(wrapper).build();
		} else {
			p = HttpParameters.library(params.getLibrary()).uri(params.getUri()).procedure(params.getProcedure()).noInboundHeaders().build();
		}
		if(segment != null) {
			segment.reportAsExternal(p);
			segment.end();
			segment = null;
		}
	}

}
