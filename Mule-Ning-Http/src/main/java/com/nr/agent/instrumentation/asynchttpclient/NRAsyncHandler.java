package com.nr.agent.instrumentation.asynchttpclient;

import com.newrelic.api.agent.GenericParameters;
import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.ning.http.client.AsyncHandler;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Weave(type = MatchType.Interface, originalName = "com.ning.http.client.AsyncHandler")
public class NRAsyncHandler<T> {

    @NewField
    private AtomicBoolean userAbortedOnStatusReceived;
    @NewField
    public Segment segment;
    @NewField
    public URI uri;
    @NewField
    private InboundWrapper inboundHeaders;
    @NewField
    private HttpResponseStatus responseStatus;

    public AsyncHandler.STATE onStatusReceived(HttpResponseStatus responseStatus) {
        // Transfer token from outer handler (stored in execute) to this inner handler
        // onStatusReceived fires before onCompleted on the same handler chain
        NingTokenCache.transferToHandler(this);
        NewRelic.getAgent().getLogger().log(java.util.logging.Level.FINE,
            "NING-DEBUG: onStatusReceived() this={0} segment={1} cacheSize={2}",
            this.getClass().getName(), segment, NingTokenCache.cacheSize());

        AsyncHandler.STATE userState = Weaver.callOriginal();
        if (userState == AsyncHandler.STATE.ABORT) {
            if (userAbortedOnStatusReceived == null) {
                userAbortedOnStatusReceived = new AtomicBoolean(false);
            }
            userAbortedOnStatusReceived.set(true);
            return AsyncHandler.STATE.CONTINUE;
        }
        this.responseStatus = responseStatus;
        return userState;
    }

    public void onThrowable(Throwable t) {
        NingTokenCache.TokenAndSegment ts = NingTokenCache.getAndClear(this);
        if (ts != null && ts.token != null) {
            ts.token.linkAndExpire();
        }
        Segment seg = segment != null ? segment : (ts != null ? ts.segment : null);
        if (seg != null) {
            seg.reportAsExternal(GenericParameters
                    .library("AsyncHttpClient")
                    .uri(uri)
                    .procedure("onThrowable")
                    .build());
            seg.end();
        }
        segment = null;
        uri = null;
        inboundHeaders = null;
        userAbortedOnStatusReceived = null;
        responseStatus = null;

        Weaver.callOriginal();
    }

    public AsyncHandler.STATE onHeadersReceived(HttpResponseHeaders headers) {
        if (!headers.isTraillingHeadersReceived()) {
            if (segment != null) {
                inboundHeaders = new InboundWrapper(new HashMap<>(headers.getHeaders()));
            }
            if (userAbortedOnStatusReceived != null && userAbortedOnStatusReceived.get()) {
                return AsyncHandler.STATE.ABORT;
            }
        }
        return Weaver.callOriginal();
    }

    @Trace(async = true)
    public T onCompleted() throws Exception {
        NingTokenCache.TokenAndSegment ts = NingTokenCache.getAndClear(this);
        NewRelic.getAgent().getLogger().log(java.util.logging.Level.FINE,
            "NING-DEBUG: onCompleted() this={0} segment={1} ts={2} tsToken={3} tsSegment={4}",
            this.getClass().getName(), segment,
            ts != null ? "found" : "null",
            ts != null && ts.token != null ? "hasToken" : "noToken",
            ts != null && ts.segment != null ? "hasSegment" : "noSegment");

        if (ts != null && ts.token != null) {
            try {
                ts.token.linkAndExpire();
                NewRelic.getAgent().getLogger().log(java.util.logging.Level.FINE,
                    "NING-DEBUG: token.linkAndExpire() succeeded");
            } catch (Throwable t) {
                NewRelic.getAgent().getLogger().log(java.util.logging.Level.FINE,
                    "NING-DEBUG: token.linkAndExpire() FAILED: {0}", t.toString());
            }
        }

        Segment seg = segment != null ? segment : (ts != null ? ts.segment : null);
        NewRelic.getAgent().getLogger().log(java.util.logging.Level.FINE,
            "NING-DEBUG: seg={0} segment={1} tsSegment={2}",
            seg, segment, ts != null ? ts.segment : "tsNull");
        if (seg != null) {
            try {
                java.net.URI reportUri = uri != null ? uri : (ts != null ? ts.uri : null);
                seg.reportAsExternal(HttpParameters
                        .library("AsyncHttpClient")
                        .uri(reportUri)
                        .procedure("onCompleted")
                        .inboundHeaders(inboundHeaders)
                        .build());
            } catch (Exception e) {
                NewRelic.getAgent().getLogger().log(java.util.logging.Level.FINE,
                    "NING-DEBUG: reportAsExternal exception: {0}", e.toString());
            } finally {
                seg.end();
                NewRelic.getAgent().getLogger().log(java.util.logging.Level.FINE,
                    "NING-DEBUG: segment.end() called for {0}", seg);
            }
        } else {
            NewRelic.getAgent().getLogger().log(java.util.logging.Level.FINE,
                "NING-DEBUG: seg is NULL - cannot close segment");
        }
        responseStatus = null;
        segment = null;
        uri = null;
        inboundHeaders = null;
        userAbortedOnStatusReceived = null;

        return Weaver.callOriginal();
    }

    private Integer getStatusCode() {
        if (responseStatus != null) {
            return responseStatus.getStatusCode();
        }
        return null;
    }

    private String getReasonMessage() {
        if (responseStatus != null) {
            return responseStatus.getStatusText();
        }
        return null;
    }
}
