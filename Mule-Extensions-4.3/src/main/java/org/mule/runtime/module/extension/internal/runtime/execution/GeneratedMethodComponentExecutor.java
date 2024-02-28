package org.mule.runtime.module.extension.internal.runtime.execution;

import org.mule.runtime.api.meta.model.ComponentModel;
import org.mule.runtime.extension.api.runtime.operation.ExecutionContext;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class GeneratedMethodComponentExecutor<M extends ComponentModel> {

	@Trace(dispatcher = true)
	public Object execute(ExecutionContext<M> executionContext) {
		return Weaver.callOriginal();
	}
}
