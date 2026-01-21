package com.nr.instrumentation.mule.batch;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRRunnable implements Runnable {
	
	private static boolean isTransformed = false;
	
	private Token token = null;
	private Runnable delegate = null;
	private String name;
	
	public NRRunnable(Runnable d, Token t) {
		this(d,t,null);
	}
	
	public NRRunnable(Runnable d, Token t,String n) {
		delegate = d;
		token = t;
		name = n;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}

	@Override
	@Trace(async=true)
	public void run() {
		if(name != null) {
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","NRRunnable","run",name);
		}
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		if(delegate != null) {
			delegate.run();
		}
	}

}
