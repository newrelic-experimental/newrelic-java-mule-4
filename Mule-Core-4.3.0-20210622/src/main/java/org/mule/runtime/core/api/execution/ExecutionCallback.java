package org.mule.runtime.core.api.execution;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.NRMuleHeaders;

@Weave(type=MatchType.Interface)
public abstract class ExecutionCallback<T> {
	
	@NewField
	public NRMuleHeaders headers;

	@Trace(dispatcher=true)
	public T process() {
		if(headers != null && ! headers.isEmpty()) {
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, headers);
		}
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ExecutionCallback",getClass().getName());
		return Weaver.callOriginal();
	}
}
