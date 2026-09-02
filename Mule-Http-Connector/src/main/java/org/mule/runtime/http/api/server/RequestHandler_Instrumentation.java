package org.mule.runtime.http.api.server;

import org.mule.runtime.http.api.domain.request.HttpRequestContext;
import org.mule.runtime.http.api.server.async.HttpResponseReadyCallback;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.httpconnector.LegacyRequestWrapper;

@Weave(type = MatchType.Interface, originalName = "org.mule.runtime.http.api.server.RequestHandler")
public abstract class RequestHandler_Instrumentation {

	@Trace(dispatcher = true)
	public void handleRequest(HttpRequestContext requestContext, HttpResponseReadyCallback responseCallback) {
		NewRelic.addCustomParameter("MuleHttpConnectorFired", "legacy-request");
		Transaction txn = NewRelic.getAgent().getTransaction();
		if (!txn.isWebTransaction()) {
			txn.convertToWebTransaction();
		}
		txn.setWebRequest(new LegacyRequestWrapper(requestContext.getRequest()));
		Weaver.callOriginal();
	}

}
