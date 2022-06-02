package org.mule.runtime.core.internal.execution;

import org.mule.runtime.core.api.execution.ExecutionCallback;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.HeaderUtils;

@Weave(type=MatchType.Interface)
public abstract class ExecutionInterceptor<T> {

	@Trace
	public T execute(ExecutionCallback<T> callback, ExecutionContext executionContext) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ExecutionInterceptor",getClass().getName(),"execute");
		HeaderUtils.acceptHeaders(callback.headers, false);
		return Weaver.callOriginal();
	}
}
