package com.nr.instrumentation.mule.api;

import java.util.function.BiConsumer;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;

public class NRBiConsumer<T> implements BiConsumer<T, Throwable> {
	
	private Segment segment = null;
	
	public NRBiConsumer(String n) {
		String name = n != null ? n : "Unknown";
		segment = NewRelic.getAgent().getTransaction().startSegment("ExecutableComponent-"+name);
	}

	@Override
	public void accept(T t, Throwable u) {
		if(segment != null) {
			segment.end();
		}
		if(u != null) {
			NewRelic.noticeError(u);
		}
	}

}
