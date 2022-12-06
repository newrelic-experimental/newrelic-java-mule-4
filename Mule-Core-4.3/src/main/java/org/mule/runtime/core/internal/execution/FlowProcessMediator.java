package org.mule.runtime.core.internal.execution;

import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core4_3.NRCoreUtils;
import com.newrelic.mule.core4_3.tracers.TracerUtils;
import java.util.HashMap;
import java.util.Map;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.api.source.MessageSource;

@Weave
public abstract class FlowProcessMediator {
	
	public void process(FlowProcessTemplate template, MessageProcessContext messageProcessContext, PhaseResultNotifier phaseResultNotifier) {
		Map<String, Object> attributes = new HashMap<>();
		FlowConstruct flowConstruct = messageProcessContext.getFlowConstruct();
		if (flowConstruct != null)
			NRCoreUtils.recordFlowConstruct(flowConstruct, attributes); 
		MessageSource msgSource = messageProcessContext.getMessageSource();
		if (msgSource != null) {
			ComponentLocation location = msgSource.getLocation();
			if (location != null)
				attributes.put("MessageSource-Location", location.getLocation()); 
		} 
		long start = System.nanoTime();
		Weaver.callOriginal();
		long end = System.nanoTime();
		ClassMethodSignature sig = new ClassMethodSignature(getClass().getName(), "process", TracerUtils.getMethodDesc(getClass(), "process"));
		String[] names = { "Custom", "ExecutionInterceptor", getClass().getName(), "process" };
		TracerUtils.processTracer(this, start, end, sig, null, names);
	}
}
