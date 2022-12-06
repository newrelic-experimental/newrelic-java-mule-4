package org.mule.runtime.core.api.processor;

import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core4_3.NRCoreUtils;
import com.newrelic.mule.core4_3.tracers.TracerUtils;
import java.util.HashMap;
import java.util.Map;
import org.mule.runtime.core.api.event.CoreEvent;

@Weave(type = MatchType.Interface)
public abstract class Processor {
	
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
