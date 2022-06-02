package org.mule.runtime.core.api.execution;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.HeaderUtils;
import com.newrelic.mule.core.NRMuleHeaders;

@Weave(type=MatchType.Interface)
public abstract class ExecutionCallback<T> {
	
	@NewField
	public NRMuleHeaders headers = null;

	@Trace(async=true)
	public T process() {
		HeaderUtils.acceptHeaders(headers, false);
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ExecutionCallback",getClass().getName());
		return Weaver.callOriginal();
	}
}
