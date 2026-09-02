package org.mule.runtime.http.api.server.async;

import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.httpconnector.LegacyResponseWrapper;

@Weave(type = MatchType.Interface, originalName = "org.mule.runtime.http.api.server.async.HttpResponseReadyCallback")
public abstract class HttpResponseReadyCallback_Instrumentation {

	public void responseReady(HttpResponse response, ResponseStatusCallback statusCallback) {
		NewRelic.addCustomParameter("MuleHttpConnectorResponseFired", "legacy-response");
		Transaction txn = NewRelic.getAgent().getTransaction();
		if (txn.isWebTransaction()) {
			txn.setWebResponse(new LegacyResponseWrapper(response));
			txn.addOutboundResponseHeaders();
			txn.markResponseSent();
		}
		Weaver.callOriginal();
	}

}
