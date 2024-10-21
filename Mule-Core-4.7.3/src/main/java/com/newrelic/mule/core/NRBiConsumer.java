package com.newrelic.mule.core;

import java.util.function.BiConsumer;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRBiConsumer<T,U> implements BiConsumer<T,U> {
	
	private String name = null;
	private Token token = null;
	
	private static boolean isTransformed = false;
	
	public NRBiConsumer(String n) {
		name = n;
		token = NewRelic.getAgent().getTransaction().getToken();
		if(!isTransformed) {
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
			isTransformed = true;
		}
	}
	
	@Override
	@Trace(async=true)
	public void accept(T t, U u) {
		if(name != null && !name.isEmpty()) {
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","CompletionHandler",name);
		}
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
	}

}
