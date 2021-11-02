package org.mule.runtime.api.component.execution;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.NRMuleHeaders;

@Weave(type=MatchType.Interface)
public abstract class CompletableCallback<T> {
	
	@NewField
	public NRMuleHeaders headers = null;
	
	@Trace
	public void complete(T var1) {
		if(headers != null && !headers.isEmpty()) {
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, headers);
		}
		Weaver.callOriginal();
	}

	@Trace
	public void error(Throwable var1) {
		NewRelic.noticeError(var1);
		if(headers != null && !headers.isEmpty()) {
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, headers);
		}
		Weaver.callOriginal();
	}


}
