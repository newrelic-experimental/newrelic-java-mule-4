package org.mule.sdk.api.http.server;

import org.mule.sdk.api.http.domain.message.request.HttpRequestContext;
import org.mule.sdk.api.http.server.async.HttpResponseReadyCallback;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.httpconnector.SdkRequestWrapper;

@Weave(type = MatchType.Interface, originalName = "org.mule.sdk.api.http.server.RequestHandler")
public abstract class RequestHandler_Instrumentation {

	@Trace(dispatcher = true)
	public void handleRequest(HttpRequestContext requestContext, HttpResponseReadyCallback responseCallback) {
		NewRelic.addCustomParameter("MuleHttpConnectorFired", "sdk-request");
		Transaction txn = NewRelic.getAgent().getTransaction();
		if (!txn.isWebTransaction()) {
			txn.convertToWebTransaction();
		}
		txn.setWebRequest(new SdkRequestWrapper(requestContext.getRequest()));
		Weaver.callOriginal();
	}

}
