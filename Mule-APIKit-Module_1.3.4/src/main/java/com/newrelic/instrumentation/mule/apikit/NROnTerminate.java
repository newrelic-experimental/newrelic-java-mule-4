package com.newrelic.instrumentation.mule.apikit;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;

public class NROnTerminate implements Runnable {
	
	private Segment segment = null;
	
	public NROnTerminate(String name) {
		segment = NewRelic.getAgent().getTransaction().startSegment(name);
	}

	@Override
	public void run() {
		if(segment != null) {
			segment.end();
			segment = null;
		}
	}

}
