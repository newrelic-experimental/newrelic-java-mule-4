package org.mule.runtime.core.api.processor;

import org.mule.runtime.core.api.event.CoreEvent;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class Processor {

	@Trace
	public CoreEvent process(CoreEvent event) {
		CoreEvent returnedEvent = Weaver.callOriginal();
		
		return returnedEvent;
	}
	
}
