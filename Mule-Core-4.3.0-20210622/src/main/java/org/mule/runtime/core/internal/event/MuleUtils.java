package org.mule.runtime.core.internal.event;

import org.mule.runtime.api.event.EventContext;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.privileged.event.BaseEventContext;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.mule.core.NRMuleHeaders;

public class MuleUtils {

	public static NRMuleHeaders getHeaders(CoreEvent event) {
		if (event != null) {
			EventContext context = event.getContext();
			if (context != null) {
				return getHeaders(context);
			} 
		}
		return null;
	}

	
	public static NRMuleHeaders getHeaders(EventContext context) {
		if(context != null && AbstractEventContext.class.isInstance(context)) {
			AbstractEventContext tmpEventCtx = (AbstractEventContext)context;
			NRMuleHeaders headers = tmpEventCtx.headers;
			if(headers != null) return headers;
			BaseEventContext rootContext = tmpEventCtx.getRootContext();
			if(rootContext != tmpEventCtx) {
				return getHeaders(rootContext);
			}
		}
		
		return null;
	}
	
	public static void setHeaders(EventContext context) {
		if(context != null && context instanceof AbstractEventContext) {
			AbstractEventContext eventCtx = (AbstractEventContext)context;
			if(eventCtx.headers == null) {
				BaseEventContext rootContext = eventCtx.getRootContext();
				if(rootContext != eventCtx) {
					setHeaders(rootContext);
				} else {
					eventCtx.headers = new NRMuleHeaders();
					NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(eventCtx.headers);	
				}
			} else if(eventCtx.headers.isEmpty()) {
				NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(eventCtx.headers);
			}
		}
		
	}
	
	public static void setHeaders(EventContext context, NRMuleHeaders headers) {
		if (context != null && context instanceof BaseEventContext) {
			BaseEventContext rootContext = ((BaseEventContext)context).getRootContext();
			if (rootContext != null && rootContext instanceof AbstractEventContext) {
				((AbstractEventContext) rootContext).headers = headers;
			} 
		}
	}
}
