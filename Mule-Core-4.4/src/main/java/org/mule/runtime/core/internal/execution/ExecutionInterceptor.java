package org.mule.runtime.core.internal.execution;

import org.mule.runtime.core.api.execution.ExecutionCallback;

import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core4_4.HeaderUtils;
import com.newrelic.mule.core4_4.tracers.TracerUtils;

@Weave(type=MatchType.Interface)
public abstract class ExecutionInterceptor<T> {

	@Trace
	public T execute(ExecutionCallback<T> callback, ExecutionContext executionContext) {
		long start = System.nanoTime();
		T result = Weaver.callOriginal();
		long end = System.nanoTime();
		ClassMethodSignature sig = new ClassMethodSignature(getClass().getName(), "execute", TracerUtils.getMethodDesc(getClass(), "execute"));
		String[] names = { "Custom", "ExecutionInterceptor", getClass().getName(), "execute" };
		TracerUtils.processTracer(this, start, end, sig, null, names);
		HeaderUtils.acceptHeaders(callback.headers);
		return result;
	}
}
