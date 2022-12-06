package org.mule.runtime.core.internal.execution;

import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.api.agent.Headers;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core4_3.NRCoreUtils;
import com.newrelic.mule.core4_3.NRMuleHeaders;
import com.newrelic.mule.core4_3.tracers.TracerUtils;
import java.util.HashMap;
import java.util.Map;
import org.mule.runtime.api.component.Component;
import org.mule.runtime.api.component.execution.CompletableCallback;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.internal.exception.MessagingException;
import org.reactivestreams.Publisher;

@Weave(type = MatchType.BaseClass)
public abstract class FlowProcessingTemplate {
	
	public CoreEvent routeEvent(CoreEvent muleEvent) {
		Map<String, Object> attributes = new HashMap<>();
		NRCoreUtils.recordCoreEvent("Input", muleEvent, attributes);
		long start = System.nanoTime();
		CoreEvent returnedEvent = (CoreEvent)Weaver.callOriginal();
		long end = System.nanoTime();
		NRCoreUtils.recordCoreEvent("Returned", returnedEvent, attributes);
		ClassMethodSignature sig = new ClassMethodSignature(getClass().getName(), "routeEvent", TracerUtils.getMethodDesc(getClass(), "routeEvent"));
		String[] names = { "Custom", "FlowProcessingTemplate", getClass().getSimpleName(), "routeEvent" };
		TracerUtils.processTracer(this, start, end, sig, attributes, names);
		return returnedEvent;
	}

	public Publisher<CoreEvent> routeEventAsync(CoreEvent event) {
		Map<String, Object> attributes = new HashMap<>();
		NRCoreUtils.recordCoreEvent("Input", event, attributes);
		long start = System.nanoTime();
		Publisher<CoreEvent> result = Weaver.callOriginal();
		long end = System.nanoTime();
		ClassMethodSignature sig = new ClassMethodSignature(getClass().getName(), "routeEventAsync", TracerUtils.getMethodDesc(getClass(), "routeEventAsync"));
		String[] names = { "Custom", "FlowProcessingTemplate", getClass().getSimpleName(), "routeEventAsync" };
		TracerUtils.processTracer(this, start, end, sig, attributes, names);
		return result;
	}

	public void sendResponseToClient(CoreEvent response, Map<String, Object> parameters, CompletableCallback<Void> callback) {
		Map<String, Object> attributes = new HashMap<>();
		NRCoreUtils.recordCoreEvent("Response", response, attributes);
		if (callback.headers == null || callback.headers.isEmpty()) {
			callback.headers = new NRMuleHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders((Headers)callback.headers);
		} 
		long start = System.nanoTime();
		Weaver.callOriginal();
		long end = System.nanoTime();
		ClassMethodSignature sig = new ClassMethodSignature(getClass().getName(), "sendResponseToClient", TracerUtils.getMethodDesc(getClass(), "sendResponseToClient"));
		String[] names = { "Custom", "FlowProcessingTemplate", getClass().getSimpleName(), "sendResponseToClient" };
		TracerUtils.processTracer(this, start, end, sig, attributes, names);
	}

	public void sendFailureResponseToClient(MessagingException exception, Map<String, Object> parameters, CompletableCallback<Void> callback) {
		CoreEvent event = exception.getEvent();
		Map<String, Object> attributes = new HashMap<>();
		NRCoreUtils.recordCoreEvent(null, event, attributes);
		Component failing = exception.getFailingComponent();
		if (failing != null) {
			ComponentLocation location = failing.getLocation();
			if (location != null)
				NRCoreUtils.recordValue(attributes, "FailingComponent", location.getLocation()); 
		} 
		NRCoreUtils.recordValue(attributes, "Handled", Boolean.valueOf(exception.handled()));
		NewRelic.noticeError((Throwable)exception, attributes);
		if (callback.headers == null || callback.headers.isEmpty()) {
			callback.headers = new NRMuleHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders((Headers)callback.headers);
		} 
		long start = System.nanoTime();
		Weaver.callOriginal();
		long end = System.nanoTime();
		ClassMethodSignature sig = new ClassMethodSignature(getClass().getName(), "sendFailureResponseToClient", TracerUtils.getMethodDesc(getClass(), "sendFailureResponseToClient"));
		String[] names = { "Custom", "FlowProcessingTemplate", getClass().getSimpleName(), "sendFailureResponseToClient" };
		TracerUtils.processTracer(this, start, end, sig, attributes, names);
	}

	public Publisher<CoreEvent> routeEventAsync(Publisher<CoreEvent> eventPub) {
		long start = System.nanoTime();
		Publisher<CoreEvent> result = Weaver.callOriginal();
		long end = System.nanoTime();
		ClassMethodSignature sig = new ClassMethodSignature(getClass().getName(), "sendFailureResponseToClient", TracerUtils.getMethodDesc(getClass(), "sendFailureResponseToClient"));
		String[] names = { "Custom", "FlowProcessingTemplate", getClass().getSimpleName(), "sendFailureResponseToClient" };
		TracerUtils.processTracer(this, start, end, sig, null, names);
		return result;
	}
}
