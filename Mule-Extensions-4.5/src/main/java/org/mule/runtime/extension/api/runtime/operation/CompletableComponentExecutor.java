package org.mule.runtime.extension.api.runtime.operation;

import org.mule.runtime.api.meta.model.ComponentModel;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.WeaveAllConstructors;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type = MatchType.Interface)
public abstract class CompletableComponentExecutor<M extends ComponentModel> {

	@Trace(dispatcher = true)
	public void execute(ExecutionContext<M> context, ExecutorCallback callback) {
		if(callback.token != null) {
			callback.token.link();
			callback.token = null;
		}
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","CompletableComponentExecutor",getClass().getSimpleName(),"execute");
		Weaver.callOriginal();
	}

	@Weave(type = MatchType.Interface)
	public static class ExecutorCallback {
		
		@NewField
		protected Token token = null;
		
		@WeaveAllConstructors
		public ExecutorCallback() {
			if (token == null) {
				Token t = NewRelic.getAgent().getTransaction().getToken();
				if (t != null && t.isActive()) {
					token = t;
				} else if(t != null) {
					t.expire();
					t = null;
				}
			}
		}
		
		@Trace(dispatcher = true)
		public void complete(Object p0) {
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ExecutorCallback",getClass().getSimpleName(),"complete");
			Weaver.callOriginal();
		}

		@Trace(dispatcher = true)
		public void error(Throwable p0) {
			if(p0 != null) NewRelic.noticeError(p0);
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ExecutorCallback",getClass().getSimpleName(),"error");
			Weaver.callOriginal();
		}
	}
}
