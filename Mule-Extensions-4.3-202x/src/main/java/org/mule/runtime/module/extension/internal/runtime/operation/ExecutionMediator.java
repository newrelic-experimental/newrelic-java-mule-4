package org.mule.runtime.module.extension.internal.runtime.operation;

import org.mule.runtime.api.meta.model.ComponentModel;
import org.mule.runtime.extension.api.runtime.operation.CompletableComponentExecutor;
import org.mule.runtime.module.extension.api.runtime.privileged.ExecutionContextAdapter;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class ExecutionMediator<M extends ComponentModel> {
	
	@Trace
	public void execute(CompletableComponentExecutor<M> executor, ExecutionContextAdapter<M> context, CompletableComponentExecutor.ExecutorCallback callback) {
		Weaver.callOriginal();
	}
}
