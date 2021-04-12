package org.mule.runtime.http.api.server;

import org.mule.runtime.http.api.domain.request.HttpRequestContext;
import org.mule.runtime.http.api.server.async.HttpResponseReadyCallback;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class RequestHandler {

	@Trace
	public void handleRequest(HttpRequestContext requestContext, HttpResponseReadyCallback responseCallback) {
//		Transaction transaction = NewRelic.getAgent().getTransaction();
//		if(!transaction.isWebTransaction()) {
//			transaction.convertToWebTransaction();
//		}
//		
//		if(requestContext != null) {
//			HttpRequest request = requestContext.getRequest();
//			if(request != null) {
//				InboundWrapper wrapper = new InboundWrapper(request);
//				NewRelic.getAgent().getTransaction().setWebRequest(wrapper);
//			}
//		}
//		if(responseCallback != null) {
//			responseCallback = new NRHttpResponseReadyCallback(responseCallback, NewRelic.getAgent().getTransaction().getToken());
//		}
		Weaver.callOriginal();
	}
}
