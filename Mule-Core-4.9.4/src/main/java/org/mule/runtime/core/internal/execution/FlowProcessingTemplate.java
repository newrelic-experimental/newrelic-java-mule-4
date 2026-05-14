package org.mule.runtime.core.internal.execution;

import java.util.HashMap;
import java.util.Map;

import org.mule.runtime.api.component.Component;
import org.mule.runtime.api.component.execution.CompletableCallback;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.privileged.exception.MessagingException;
import org.reactivestreams.Publisher;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.NRCoreUtils;
import com.newrelic.mule.core.NRMuleHeaders;
import org.mule.runtime.api.event.EventContext;
import org.mule.runtime.core.internal.event.AbstractEventContext;

@Weave(type=MatchType.BaseClass)
public abstract class FlowProcessingTemplate {

	// PHASE 2: Re-enabled
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
		// Store token on event context for async thread linking
		try {
			if(event != null) {
				EventContext ctx = event.getContext();
				if(ctx instanceof AbstractEventContext) {
					AbstractEventContext actx = (AbstractEventContext) ctx;
					if(actx.token == null) {
						actx.token = NewRelic.getAgent().getTransaction().getToken();
					}
				}
			}
		} catch (Exception e) { }
		return Weaver.callOriginal();
	}

	@Trace
	public void sendResponseToClient(CoreEvent response, Map<String, Object> parameters, CompletableCallback<Void> callback) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		NRCoreUtils.recordCoreEvent("Response", response, attributes);
		NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);
		if(callback.headers == null || callback.headers.isEmpty()) {
			callback.headers = new NRMuleHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(callback.headers);
		}
		Weaver.callOriginal();
	}

	@Trace
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
		if(callback.headers == null || callback.headers.isEmpty()) {
			callback.headers = new NRMuleHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(callback.headers);
		}
		Weaver.callOriginal();
	}

	@Trace
	public Publisher<CoreEvent> routeEventAsync(Publisher<CoreEvent> eventPub) {
		return Weaver.callOriginal();
	}
}