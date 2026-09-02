package org.mule.sdk.api.http.server.async;

import org.mule.sdk.api.http.domain.message.response.HttpResponse;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.httpconnector.SdkResponseWrapper;

@Weave(type = MatchType.Interface, originalName = "org.mule.sdk.api.http.server.async.HttpResponseReadyCallback")
public abstract class HttpResponseReadyCallback_Instrumentation {

	public void responseReady(HttpResponse response, ResponseStatusCallback statusCallback) {
		NewRelic.addCustomParameter("MuleHttpConnectorResponseFired", "sdk-response");
		Transaction txn = NewRelic.getAgent().getTransaction();
		if (txn.isWebTransaction()) {
			txn.setWebResponse(new SdkResponseWrapper(response));
			txn.addOutboundResponseHeaders();
			txn.markResponseSent();
		}
		Weaver.callOriginal();
	}

}
