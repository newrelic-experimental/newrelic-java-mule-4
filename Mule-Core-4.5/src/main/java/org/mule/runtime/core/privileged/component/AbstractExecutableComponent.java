package org.mule.runtime.core.privileged.component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.mule.runtime.api.component.AbstractComponent;
import org.mule.runtime.api.component.execution.ExecutionResult;
import org.mule.runtime.api.component.execution.InputEvent;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.internal.event.MuleUtils;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.HeaderUtils;
import com.newrelic.mule.core.NRMuleHeaders;

import reactor.core.publisher.Mono;

@Weave
public abstract class AbstractExecutableComponent extends AbstractComponent {

	@Trace(dispatcher=true)
	public CompletableFuture<ExecutionResult> execute(InputEvent paramInputEvent) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.addCustomAttribute("InputParameter", "InputEvent");
		traced.setMetricName(new String[] {"Custom","AbstractExecutableComponent",getClass().getSimpleName(),"execute"});
		CompletableFuture<ExecutionResult> f = Weaver.callOriginal();
		ComponentLocation location = getLocation();
		if(location != null) {
			String locationStr = location.getLocation();
			if(locationStr != null && !locationStr.isEmpty()) {
				traced.addCustomAttribute("LocationString", locationStr);
			}
		}
		return f;
	}

	@Trace(dispatcher=true)
	public CompletableFuture<Event> execute(Event paramEvent) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.setMetricName(new String[] {"Custom","AbstractExecutableComponent",getClass().getSimpleName(),"execute"});
		traced.addCustomAttribute("InputParameter", "Event");
		CompletableFuture<Event> f = Weaver.callOriginal();
		ComponentLocation location = getLocation();
		if(location != null) {
			String locationStr = location.getLocation();
			if(locationStr != null && !locationStr.isEmpty()) {
				traced.addCustomAttribute("LocationString", locationStr);
			}
		}
		return f;
	}

	@Trace(dispatcher=true)
	public CompletableFuture<Event> execute(Event event, Consumer<CoreEvent.Builder> childEventContributor) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.setMetricName(new String[] {"Custom","AbstractExecutableComponent",getClass().getSimpleName(),"execute"});
		traced.addCustomAttribute("InputParameter", "Event,Consumer");
		CompletableFuture<Event> f = Weaver.callOriginal();
		ComponentLocation location = getLocation();
		if(location != null) {
			String locationStr = location.getLocation();
			if(locationStr != null && !locationStr.isEmpty()) {
				traced.addCustomAttribute("LocationString", locationStr);
			}
		}
		return f;
	}
	
	@SuppressWarnings("unused")
	private Mono<CoreEvent> doProcess(CoreEvent event) {
		NRMuleHeaders headers = MuleUtils.getHeaders((CoreEvent)event);
		HeaderUtils.acceptHeaders(headers);
		return Weaver.callOriginal();
	}
}
