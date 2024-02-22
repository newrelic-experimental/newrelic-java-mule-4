package org.mule.runtime.core.privileged.component;

import java.util.concurrent.CompletableFuture;

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
		CompletableFuture<ExecutionResult> f = Weaver.callOriginal();
		ComponentLocation location = getLocation();
		if(location != null) {
			String locationStr = location.getLocation();
			if(locationStr != null && !locationStr.isEmpty()) {
				NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","AbstractExecutableComponent",getClass().getSimpleName(),"execute"});
				NewRelic.getAgent().getTracedMethod().addCustomAttribute("LocationString", locationStr);
			}
		}
		return f;
	}

	@Trace(dispatcher=true)
	public CompletableFuture<Event> execute(Event paramEvent) {
		CompletableFuture<Event> f = Weaver.callOriginal();
		ComponentLocation location = getLocation();
		if(location != null) {
			String locationStr = location.getLocation();
			if(locationStr != null && !locationStr.isEmpty()) {
				TracedMethod traced = NewRelic.getAgent().getTracedMethod();
				traced.addCustomAttribute("Location", locationStr);
				traced.setMetricName(new String[] {"Custom","AbstractExecutableComponent",getClass().getSimpleName(),"execute",locationStr});
				
			}
		}
		return f;
	}
	
	@SuppressWarnings("unused")
	private Mono<CoreEvent> doProcess(CoreEvent event) {
		NRMuleHeaders headers = MuleUtils.getHeaders(event);
		HeaderUtils.acceptHeaders(headers);
		return Weaver.callOriginal();
	}

}
