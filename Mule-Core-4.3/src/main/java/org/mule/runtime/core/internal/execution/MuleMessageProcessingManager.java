package org.mule.runtime.core.internal.execution;

import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core4_3.NRCoreUtils;
import com.newrelic.mule.core4_3.tracers.TracerUtils;
import java.util.HashMap;
import java.util.Map;
import org.mule.runtime.core.api.construct.FlowConstruct;

@Weave
public class MuleMessageProcessingManager {
	
	public void processMessage(FlowProcessTemplate messageProcessTemplate, MessageProcessContext messageProcessContext) {
		String[] names;
		Map<String, Object> attributes = new HashMap<>();
		FlowConstruct flow = messageProcessContext.getFlowConstruct();
		if (flow != null) {
			NRCoreUtils.recordFlowConstruct(flow, attributes);
			names = new String[] { "Custom", "MuleMessageProcessingManager", "processMessage", flow.getName() };
		} else {
			names = new String[] { "Custom", "MuleMessageProcessingManager", "processMessage", "UnknownFlow" };
		} 
		long start = System.nanoTime();
		Weaver.callOriginal();
		long end = System.nanoTime();
		ClassMethodSignature sig = new ClassMethodSignature(getClass().getName(), "sendFailureResponseToClient", TracerUtils.getMethodDesc(getClass(), "sendFailureResponseToClient"));
		TracerUtils.processTracer(this, start, end, sig, attributes, names);
	}
}
