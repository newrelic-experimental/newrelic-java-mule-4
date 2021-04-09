package com.mulesoft.mule.runtime.module.batch.engine;

import org.mule.runtime.core.api.event.CoreEvent;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.BaseClass)
public abstract class BatchProcessingTemplate {

	@Trace
	public CoreEvent process(BatchJobInstanceAdapter jobInstance, CoreEvent event) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","BatchProcessingTemplate",getClass().getSimpleName(),"process");
		return Weaver.callOriginal();
	}
	
	@Trace
	protected void onException(BatchJobInstanceAdapter p0, Exception p1, CoreEvent p2) {
		NewRelic.noticeError(p1);
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","BatchProcessingTemplate",getClass().getSimpleName(),"onException");
		Weaver.callOriginal();
	}

	@Trace
	protected void onSuccess(BatchJobInstanceAdapter p0, CoreEvent p1) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","BatchProcessingTemplate",getClass().getSimpleName(),"onSuccess");
		Weaver.callOriginal();
	}

}
