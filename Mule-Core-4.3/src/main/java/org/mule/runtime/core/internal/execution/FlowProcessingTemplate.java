package org.mule.runtime.core.internal.execution;

import java.util.HashMap;
import java.util.Map;

import org.mule.runtime.api.component.Component;
import org.mule.runtime.api.component.execution.CompletableCallback;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.internal.exception.MessagingException;
import org.reactivestreams.Publisher;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.NRCoreUtils;

@Weave(type=MatchType.BaseClass)
public abstract class FlowProcessingTemplate {

	@Trace
	public CoreEvent routeEvent(CoreEvent muleEvent) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		NRCoreUtils.recordCoreEvent("Input", muleEvent, attributes);
		CoreEvent returnedEvent = Weaver.callOriginal();
		NRCoreUtils.recordCoreEvent("Returned", returnedEvent, attributes);
		NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);

		return returnedEvent;
	}

	@Trace
	public Publisher<CoreEvent> routeEventAsync(CoreEvent event) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		NRCoreUtils.recordCoreEvent("Input", event, attributes);
		return Weaver.callOriginal();
	}

	@Trace
	public void sendResponseToClient(CoreEvent response, Map<String, Object> parameters, CompletableCallback<Void> callback) { 
		Map<String, Object> attributes = new HashMap<String, Object>();
		NRCoreUtils.recordCoreEvent("Response", response, attributes);
		NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);
		if(callback.token == null) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			if(token != null && token.isActive()) {
				callback.token = token;
			} else if(token != null) {
				token.expire();
				token = null;
			}
		}
		Weaver.callOriginal();
	}
	
	public void sendFailureResponseToClient(MessagingException exception, Map<String, Object> parameters,CompletableCallback<Void> callback) {
		CoreEvent event = exception.getEvent();
		Map<String, Object> attributes = new HashMap<String, Object>();
		NRCoreUtils.recordCoreEvent(null, event, attributes);
		Component failing = exception.getFailingComponent();
		if (failing != null) {
			ComponentLocation location = failing.getLocation();
			if(location != null) {
				NRCoreUtils.recordValue(attributes,"FailingComponent", location.getLocation());
			}
		}
		NRCoreUtils.recordValue(attributes, "Handled", exception.handled());
		NewRelic.noticeError(exception, attributes);
		if(callback.token == null) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			if(token != null && token.isActive()) {
				callback.token = token;
			} else if(token != null) {
				token.expire();
				token = null;
			}
		}
		Weaver.callOriginal();
	}
	
	@Trace
	public Publisher<CoreEvent> routeEventAsync(Publisher<CoreEvent> eventPub) {
		return Weaver.callOriginal();
	}
}
