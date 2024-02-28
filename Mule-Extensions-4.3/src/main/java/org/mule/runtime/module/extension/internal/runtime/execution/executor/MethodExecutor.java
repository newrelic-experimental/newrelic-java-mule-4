package org.mule.runtime.module.extension.internal.runtime.execution.executor;

import org.mule.runtime.extension.api.runtime.operation.ExecutionContext;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type = MatchType.Interface)
public abstract class MethodExecutor {

	@SuppressWarnings("rawtypes")
	@Trace
	public Object execute(ExecutionContext executionContext) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","MethodExecutor",getClass().getSimpleName(),"execute");
		return Weaver.callOriginal();
	}
}
