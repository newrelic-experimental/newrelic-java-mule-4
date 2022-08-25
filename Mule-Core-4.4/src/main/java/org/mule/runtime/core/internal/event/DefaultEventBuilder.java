package org.mule.runtime.core.internal.event;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.core.internal.message.InternalEvent;
import org.mule.runtime.core.privileged.event.BaseEventContext;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class DefaultEventBuilder {
	
	public InternalEvent build() {
		InternalEvent event = Weaver.callOriginal();
		
		String  corrId = event.getCorrelationId();
		
		NewRelic.addCustomParameter("CorrelationId", corrId);
		
		BaseEventContext eventCtx = event.getContext();
		if(eventCtx != null) {
			ComponentLocation location = eventCtx.getOriginatingLocation();
			if(location != null) {
				NewRelic.addCustomParameter("Location", location.getLocation());
			}
		}
		return event;
		
	}

}
