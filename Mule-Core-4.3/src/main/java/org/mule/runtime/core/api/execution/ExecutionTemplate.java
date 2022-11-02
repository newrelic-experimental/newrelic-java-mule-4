package org.mule.runtime.core.api.execution;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.NRMuleHeaders;

@Weave(type=MatchType.Interface)
public abstract class ExecutionTemplate<T> {

	@Trace(dispatcher=true)
	public T execute(ExecutionCallback<T> callback) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ExecutionTemplate",getClass().getName());
		if(callback.headers == null || callback.headers.isEmpty()) {
			callback.headers = new NRMuleHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(callback.headers);
		}
		return Weaver.callOriginal();
	}
}
