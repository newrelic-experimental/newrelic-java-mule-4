package org.mule.runtime.core.api.processor;

import java.util.HashMap;
import java.util.Map;

import org.mule.runtime.core.api.event.CoreEvent;

import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core4_4.NRCoreUtils;
import com.newrelic.mule.core4_4.tracers.TracerUtils;

@Weave(type=MatchType.Interface)
public abstract class Processor {

	@Trace
	public CoreEvent process(CoreEvent event) {
		Map<String, Object> attributes = new HashMap<>();
		NRCoreUtils.recordCoreEvent("Input", event, attributes);
		long start = System.nanoTime();
		CoreEvent returnedEvent = (CoreEvent)Weaver.callOriginal();
		CoreEvent returnEvent = returnedEvent;
		long end = System.nanoTime();
		NRCoreUtils.recordCoreEvent("Returned", returnedEvent, attributes);
		ClassMethodSignature sig = new ClassMethodSignature(getClass().getName(), "process", TracerUtils.getMethodDesc(getClass(), "process"));
		String[] names = { "Custom", "Processor", getClass().getSimpleName(), "process" };
		TracerUtils.processTracer(this, start, end, sig, attributes, names);
		return returnEvent;
	}
	
}
