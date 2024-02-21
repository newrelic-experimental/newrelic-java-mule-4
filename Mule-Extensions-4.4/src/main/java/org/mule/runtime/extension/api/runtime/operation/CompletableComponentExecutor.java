package org.mule.runtime.extension.api.runtime.operation;

import org.mule.runtime.api.meta.model.ComponentModel;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type = MatchType.Interface)
public abstract class CompletableComponentExecutor<M extends ComponentModel> {

	@Trace(dispatcher = true)
	public void execute(ExecutionContext<M> var1, ExecutorCallback var2) {
		Weaver.callOriginal();
	}

	@Weave(type = MatchType.Interface)
	public static class ExecutorCallback {
		
		@Trace(dispatcher = true)
		public void complete(Object p0) {
			Weaver.callOriginal();
		}

		@Trace(dispatcher = true)
		public void error(Throwable p0) {
			Weaver.callOriginal();
		}
	}
}
