package org.mule.runtime.http.api.client;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.mule.runtime.http.api.client.auth.HttpAuthentication;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.httpclient.InboundWrapper;
import com.nr.instrumentation.mule.httpclient.NRBiConsumer;

@Weave(type=MatchType.Interface)
public abstract class HttpClient {

	@Trace(dispatcher=true)
	public HttpResponse send(HttpRequest request, int responseTimeout, boolean followRedirects,HttpAuthentication authentication) {
		HttpResponse resp = Weaver.callOriginal();
		InboundWrapper inWrapper = new InboundWrapper(resp);
		URI uri = request.getUri();
		HttpParameters params = HttpParameters.library("Mule-HttpClient").uri(uri).procedure("sendAsync").inboundHeaders(inWrapper).build();
		NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		
		return resp;
	}

	@Trace(dispatcher=true)
	public CompletableFuture<HttpResponse> sendAsync(HttpRequest request, int responseTimeout, boolean followRedirects,HttpAuthentication authentication) {
		CompletableFuture<HttpResponse> future = Weaver.callOriginal();
		URI uri = request.getUri();
		HttpParameters params = HttpParameters.library("Mule-HttpClient").uri(uri).procedure("sendAsync").noInboundHeaders().build();
		
		return future.whenComplete(new NRBiConsumer(params));
	}

}
