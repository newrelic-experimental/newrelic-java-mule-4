package org.mule.runtime.core.api.processor;

import java.util.HashMap;
import java.util.Map;

import org.mule.runtime.api.component.Component;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.core.api.event.CoreEvent;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.NRCoreUtils;

@Weave(type=MatchType.Interface)
public abstract class Processor {

	@Trace
	public CoreEvent process(CoreEvent event) {
		CoreEvent returnedEvent = Weaver.callOriginal();
		try {
			String processorName = getClass().getSimpleName();
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom", "Mule", "Processor", processorName);
		} catch (Throwable t) { }
		return returnedEvent;
	}

}
