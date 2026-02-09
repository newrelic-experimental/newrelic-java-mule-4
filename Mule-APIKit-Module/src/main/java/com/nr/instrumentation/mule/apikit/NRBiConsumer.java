package com.nr.instrumentation.mule.apikit;

import java.util.function.BiConsumer;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;

public class NRBiConsumer<T> implements BiConsumer<T, Throwable> {
	
	private Segment segment = null;
	
	public NRBiConsumer(String n) {
		String name = n != null ? n : "Unknown";
		segment = NewRelic.getAgent().getTransaction().startSegment("APIKit-"+name);
	}

	@Override
	public void accept(T t, Throwable u) {
		if(segment != null) {
			segment.end();
			segment = null;
		}
		if(u != null) {
			NewRelic.noticeError(u);
		}
	}

}
