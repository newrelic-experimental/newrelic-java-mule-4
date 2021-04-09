package org.mule.service.http.impl.service.server.grizzly;

import org.mule.runtime.http.api.domain.message.request.HttpRequest;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class ListenerRequestMatcher {

	public abstract String getPath();
	
	@Trace
	public boolean matches(final HttpRequest request) {
		boolean b = Weaver.callOriginal();
		if(b) {
			NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "MuleHttp", getPath());
		}
		
		return b;
	}

}
