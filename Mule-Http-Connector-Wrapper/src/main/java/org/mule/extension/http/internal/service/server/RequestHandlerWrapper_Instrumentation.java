package org.mule.extension.http.internal.service.server;

import org.mule.runtime.http.api.domain.request.HttpRequestContext;
import org.mule.runtime.http.api.server.async.HttpResponseReadyCallback;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(originalName = "org.mule.extension.http.internal.service.server.RequestHandlerWrapper")
public abstract class RequestHandlerWrapper_Instrumentation {

	public void handleRequest(HttpRequestContext requestContext, HttpResponseReadyCallback responseCallback) {
		NewRelic.addCustomParameter("IsWebAtRequestHandlerWrapperEntry",
				String.valueOf(NewRelic.getAgent().getTransaction().isWebTransaction()));
		Weaver.callOriginal();
	}

}
