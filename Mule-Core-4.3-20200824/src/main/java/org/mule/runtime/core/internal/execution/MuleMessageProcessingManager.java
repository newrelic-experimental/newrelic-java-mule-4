package org.mule.runtime.core.internal.execution;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public class MuleMessageProcessingManager {

	@Trace
	public void processMessage(FlowProcessTemplate messageProcessTemplate, MessageProcessContext messageProcessContext) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","MuleMessageProcessingManager","processMessage",messageProcessContext.getFlowConstruct().getName()});
		Weaver.callOriginal();
	}
}
