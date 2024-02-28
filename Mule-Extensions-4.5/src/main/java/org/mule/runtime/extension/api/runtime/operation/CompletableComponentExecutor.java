package org.mule.runtime.extension.api.runtime.operation;

import java.util.logging.Level;

import org.mule.runtime.api.meta.model.ComponentModel;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type = MatchType.Interface)
public abstract class CompletableComponentExecutor<M extends ComponentModel> {

	@Trace(dispatcher = true)
	public void execute(ExecutionContext<M> context, ExecutorCallback callback) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","CompletableComponentExecutor",getClass().getSimpleName(),"execute");
		Weaver.callOriginal();
	}

	@Weave(type = MatchType.Interface)
	public static class ExecutorCallback {
		
		@Trace
		public void complete(Object p0) {
			NewRelic.getAgent().getLogger().log(Level.FINE, new Exception("ExecutorCallback Call"), "Call to {0}.complete({1})", getClass(), p0);
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ExecutorCallback",getClass().getSimpleName(),"complete");
			Weaver.callOriginal();
		}

		@Trace
		public void error(Throwable p0) {
			if(p0 != null) NewRelic.noticeError(p0);
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ExecutorCallback",getClass().getSimpleName(),"error");
			Weaver.callOriginal();
		}
	}
}
