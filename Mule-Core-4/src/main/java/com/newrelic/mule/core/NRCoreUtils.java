package com.newrelic.mule.core;

import java.util.Map;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.event.EventContext;
import org.mule.runtime.core.api.event.CoreEvent;

public class NRCoreUtils {

	public static void recordCoreEvent(String prefix, CoreEvent event, Map<String, Object> attributes) {
		String prepend = prefix != null ? prefix + "-CoreEvent-" : "CoreEvent-";
		recordValue(attributes, prepend+"CorrelationId", event.getCorrelationId());
		EventContext eventContext = event.getContext();
		if(eventContext != null) {
			recordValue(attributes, prepend+"ID", eventContext.getId());
			ComponentLocation origLoc = eventContext.getOriginatingLocation();
			if(origLoc != null) {
				recordValue(attributes, prepend+"OriginatingLocation", origLoc.getLocation());
			}
		}
	}
	
	public static void recordValue(Map<String,Object> attributes, String key, Object value) {
		if(key != null && !key.isEmpty() && value != null) {
			attributes.put(key, value);
		}
	}
}
