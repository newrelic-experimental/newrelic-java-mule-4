package com.ning.http.client;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.agent.instrumentation.asynchttpclient.NRAsyncHandler;
import com.nr.agent.instrumentation.asynchttpclient.NingTokenCache;
import com.nr.agent.instrumentation.asynchttpclient.OutboundWrapper;

import java.net.URI;
import java.net.URISyntaxException;

@Weave(type = MatchType.Interface)
public class AsyncHttpProvider {

    public <T> ListenableFuture<T> execute(Request request, NRAsyncHandler<T> handler) {

        URI uri = null;
        try {
            uri = new URI(request.getUrl());
            String scheme = uri.getScheme();

            if ((scheme == null || scheme.equals("http") || scheme.equals("https"))
                    && null != AgentBridge.getAgent().getTransaction(false)
                    && AgentBridge.getAgent().getTransaction().isStarted()) {
                Segment segment = NewRelic.getAgent().getTransaction().startSegment("execute");
                segment.addOutboundRequestHeaders(new OutboundWrapper(request));

                handler.uri = uri;
                handler.segment = segment;
                // Store token keyed by segment identity — segment is shared across handler chain
                Token token = NewRelic.getAgent().getTransaction().getToken();
                NingTokenCache.store(handler, token, segment, uri);
                NewRelic.getAgent().getLogger().log(java.util.logging.Level.FINE,
                    "NING-DEBUG: execute() stored token for handler={0} cacheSize={1}",
                    handler.getClass().getName(), NingTokenCache.cacheSize());
            }
        } catch (URISyntaxException uriSyntaxException) {
        }

        return Weaver.callOriginal();
    }
}
