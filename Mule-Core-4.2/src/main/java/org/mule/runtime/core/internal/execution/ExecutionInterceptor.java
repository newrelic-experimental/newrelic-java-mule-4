package org.mule.runtime.core.internal.execution;

import org.mule.runtime.core.api.execution.ExecutionCallback;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class ExecutionInterceptor<T> {

	@Trace(dispatcher=true)
	public T execute(ExecutionCallback<T> callback, ExecutionContext executionContext) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ExecutionInterceptor",getClass().getName(),"execute");
		return Weaver.callOriginal();
	}
}
