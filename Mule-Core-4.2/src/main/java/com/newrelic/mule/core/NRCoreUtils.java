package com.newrelic.mule.core;

import java.util.Map;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.event.EventContext;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.FlowConstruct;
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
	
	public static void recordCoreEvent(String prefix, Event event, Map<String, Object> attributes) {
		String prepend = prefix != null ? prefix + "-Event-" : "Event-";
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
	
	public static void recordFlowConstruct(FlowConstruct flow, Map<String,Object> attributes) {
		recordValue(attributes, "Flow-Name", flow.getName());
		ComponentLocation location = flow.getLocation();
		if(location != null) {
			recordValue(attributes, "Flow-Location", location.getLocation());
		}
		recordMuleContext(flow.getMuleContext(), attributes);
		recordValue(attributes,"Flow-ServerId",flow.getServerId());
		recordValue(attributes,"Flow-UniqueId",flow.getUniqueIdString());
	}
	
	@SuppressWarnings("deprecation")
	public static void recordMuleContext(MuleContext context, Map<String,Object> attributes) {
		if (context != null) {
			recordValue(attributes, "MuleContext-ClusterId", context.getClusterId());
			recordValue(attributes, "MuleContext-Id", context.getId());
			recordValue(attributes, "MuleContext-UniqueID", context.getUniqueIdString());
		}
	}
	
	public static void recordValue(Map<String,Object> attributes, String key, Object value) {
		if(key != null && !key.isEmpty() && value != null) {
			attributes.put(key, value);
		}
	}
}
