package org.mule.runtime.api.component.execution;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.HeaderUtils;
import com.newrelic.mule.core.NRMuleHeaders;

@Weave(type=MatchType.Interface)
public abstract class CompletableCallback<T> {
	
	@NewField
	public NRMuleHeaders headers = null;
	
	@Trace(dispatcher = true)
	public void complete(T var1) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","CompletableCallback","complete");
		HeaderUtils.acceptHeaders(headers);
		headers = null;
		Weaver.callOriginal();
	}

	@Trace(dispatcher = true)
	public void error(Throwable var1) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","CompletableCallback","error");
		NewRelic.noticeError(var1);
		HeaderUtils.acceptHeaders(headers);
		headers = null;
		Weaver.callOriginal();
	}


}
