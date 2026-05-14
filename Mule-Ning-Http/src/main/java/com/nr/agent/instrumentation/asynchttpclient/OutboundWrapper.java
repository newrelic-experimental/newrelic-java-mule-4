package com.nr.agent.instrumentation.asynchttpclient;

import java.util.Collections;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;
import com.ning.http.client.Request;

public class OutboundWrapper implements OutboundHeaders {

    private final Request request;

    public OutboundWrapper(Request request) {
        this.request = request;
    }

    @Override
    public HeaderType getHeaderType() {
        return HeaderType.HTTP;
    }

    @Override
    public void setHeader(String name, String value) {
        request.getHeaders().put(name, Collections.singletonList(value));
    }
}
