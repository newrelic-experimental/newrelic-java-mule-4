package com.newrelic.mule.core;

import java.util.function.Consumer;

import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.internal.event.MuleUtils;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

public class NREventConsumer implements Consumer<CoreEvent> {
	
	private static boolean isTransformed = false;
	
	private String name = null;
	
	public NREventConsumer(String n) {
		name = n;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}
	
	public NREventConsumer() {
		this(null);
	}
	
	@Override
	@Trace(dispatcher=true)
	public void accept(CoreEvent event) {
		NRMuleHeaders headers = MuleUtils.getHeaders(event);
		HeaderUtils.acceptHeaders(headers, true);
		if(name != null) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventConsumer",name});
		}
	}

}
