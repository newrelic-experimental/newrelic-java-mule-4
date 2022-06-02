package org.mule.runtime.core.api.retry;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class RetryCallback {

	@Trace
	public void doWork(RetryContext context) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","RetryCallback",getClass().getSimpleName(),"doWork");
		Weaver.callOriginal();
	}
}
