package com.nr.instrumentation.mule.scheduler;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.Trace;

public class NRRunnable implements Runnable {
	
	private Runnable delegate = null;
	
	private NRMuleHeaders headers = null;

	private static boolean isTransformed = false;
	
	public NRRunnable(Runnable d, NRMuleHeaders h) {
		delegate = d;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}

	@Override
	@Trace(dispatcher=true)
	public void run() {
		HeaderUtils.acceptHeaders(headers);
		if(delegate != null) {
			delegate.run();
		}
	}

}
