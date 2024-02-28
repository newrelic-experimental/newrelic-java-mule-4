package org.mule.runtime.core.internal.execution;

import java.util.HashMap;
import java.util.Map;

import org.mule.runtime.core.api.construct.FlowConstruct;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.NRCoreUtils;

@Weave
public class MuleMessageProcessingManager {

	@Trace
	public void processMessage(FlowProcessTemplate messageProcessTemplate, MessageProcessContext messageProcessContext) {
		
		FlowConstruct flow = messageProcessContext.getFlowConstruct();
		if(flow != null) {
			Map<String, Object> attributes = new HashMap<String, Object>();
			NRCoreUtils.recordFlowConstruct(flow , attributes);
			TracedMethod traced = NewRelic.getAgent().getTracedMethod();
			traced.addCustomAttributes(attributes);
			traced.setMetricName(new String[] {"Custom","MuleMessageProcessingManager","processMessage",messageProcessContext.getFlowConstruct().getName()});
		}
		Weaver.callOriginal();
	}
}
