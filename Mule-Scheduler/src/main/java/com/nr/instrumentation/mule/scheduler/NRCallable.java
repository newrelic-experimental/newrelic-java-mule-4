package com.nr.instrumentation.mule.scheduler;

import java.util.concurrent.Callable;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRCallable<V> implements Callable<V> {
	
	private Callable<V> delegate = null;
	
	private Token token = null;
	
	private static boolean isTransformed = false;
	
	public NRCallable(Callable<V> c, Token t) {
		delegate = c;
		token = t;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}

	@Override
	@Trace(async=true)
	public V call() throws Exception {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		if(delegate != null) {
			return delegate.call();
		}
		return null;
	}

}
