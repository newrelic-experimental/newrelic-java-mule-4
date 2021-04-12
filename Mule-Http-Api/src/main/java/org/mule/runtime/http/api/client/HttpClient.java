package org.mule.runtime.http.api.client;

import java.util.concurrent.CompletableFuture;

import org.mule.runtime.http.api.client.auth.HttpAuthentication;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.InboundHeaders;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.http.InboundWrapper;
import com.nr.instrumentation.mule.http.NRBiConsumer;
import com.nr.instrumentation.mule.http.OutboundWrapper;

@Weave(type=MatchType.Interface)
public abstract class HttpClient {

	@Trace
	public HttpResponse send(HttpRequest request, int responseTimeout, boolean followRedirects, HttpAuthentication authentication) {
		OutboundWrapper wrapper = new OutboundWrapper(request);
		NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(wrapper);
		request = wrapper.getRequest();
		HttpResponse response = Weaver.callOriginal();
		
		InboundHeaders inboundHeaders = new InboundWrapper(response);
		HttpParameters params = HttpParameters.library("Mule-Http").uri(request.getUri()).procedure("send").inboundHeaders(inboundHeaders).build();
		NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		return response;
	}
	
	@Trace
	public CompletableFuture<HttpResponse> sendAsync(HttpRequest request, int responseTimeout, boolean followRedirects,HttpAuthentication authentication) {
		OutboundWrapper wrapper = new OutboundWrapper(request);
		NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(wrapper);
		request = wrapper.getRequest();
		NRBiConsumer consumer = new NRBiConsumer("HttpClient",request.getUri(),"sendAsync");
		CompletableFuture<HttpResponse> result = Weaver.callOriginal();
		return result.whenComplete(consumer);
	}
}
