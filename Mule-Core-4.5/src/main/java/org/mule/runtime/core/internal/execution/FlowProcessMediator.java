package org.mule.runtime.core.internal.execution;

import java.util.HashMap;
import java.util.Map;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.api.source.MessageSource;
import org.mule.sdk.api.runtime.source.DistributedTraceContextManager;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.NRCoreUtils;
import java.util.Optional;

@Weave
public abstract class FlowProcessMediator {

	@Trace
	public void process(FlowProcessTemplate template,MessageProcessContext messageProcessContext, Optional<DistributedTraceContextManager> distributedTraceContextManager) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		FlowConstruct flowConstruct = messageProcessContext.getFlowConstruct();
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		if(flowConstruct != null) {
			NRCoreUtils.recordFlowConstruct(flowConstruct, attributes);
		}
		MessageSource msgSource = messageProcessContext.getMessageSource();
		if(msgSource != null) {
			ComponentLocation location = msgSource.getLocation();
			if(location != null) {
				traced.addCustomAttribute("MessageSource-Location", location.getLocation());
			}
		}
		traced.addCustomAttributes(attributes);
		Weaver.callOriginal();
	}
}
