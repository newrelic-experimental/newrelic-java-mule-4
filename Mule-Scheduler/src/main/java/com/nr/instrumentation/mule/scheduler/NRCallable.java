package com.nr.instrumentation.mule.scheduler;

import java.util.concurrent.Callable;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransportType;

public class NRCallable<V> implements Callable<V> {
	
	private Callable<V> delegate = null;
	
	private NRMuleHeaders headers = null;
	
	private static boolean isTransformed = false;
	
	public NRCallable(Callable<V> c, NRMuleHeaders h) {
		delegate = c;
		headers = h;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}

	@Override
	@Trace(dispatcher = true)
	public V call() throws Exception {
		if(headers != null && !headers.isEmpty()) {
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, headers);
		} else {
			NewRelic.getAgent().getTransaction().ignore();
		}
		if(delegate != null) {
			return delegate.call();
		}
		return null;
	}

}
