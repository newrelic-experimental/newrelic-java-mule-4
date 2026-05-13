package com.nr.instrumentation.mule.http;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import org.mule.runtime.http.api.domain.request.HttpRequestContext;
import org.mule.runtime.http.api.server.RequestHandler;
import org.mule.runtime.http.api.server.async.HttpResponseReadyCallback;

public class NRRequestHandler implements RequestHandler {

    private final RequestHandler delegate;
    private final String path;
    private static boolean isTransformed = false;

    public NRRequestHandler(RequestHandler delegate, String path) {
        this.delegate = delegate;
        this.path = path;
        if(!isTransformed) {
            isTransformed = true;
            AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
        }
    }

    @Override
    @Trace(dispatcher = true)
    public void handleRequest(HttpRequestContext requestContext, HttpResponseReadyCallback responseCallback) {
        String method = requestContext.getRequest().getMethod();
        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW,false,"MuleHttp","MuleHttpRequest",path + " (" + method + ")");
        delegate.handleRequest(requestContext, responseCallback);
    }
}
