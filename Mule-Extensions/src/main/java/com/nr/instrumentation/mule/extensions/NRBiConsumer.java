package com.nr.instrumentation.mule.extensions;

import java.util.function.BiConsumer;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;

public class NRBiConsumer<T> implements BiConsumer<T, Throwable> {
	
	private Segment segment = null;
	
	public NRBiConsumer(String segmentName) {
		segment = NewRelic.getAgent().getTransaction().startSegment(segmentName);
	}
	

	@Override
	public void accept(T t, Throwable u) {
		if(segment != null) {
			segment.end();
			segment = null;
		}
	}

}
