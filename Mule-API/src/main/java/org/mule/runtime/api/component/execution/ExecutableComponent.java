package org.mule.runtime.api.component.execution;

import java.util.concurrent.CompletableFuture;
//import java.util.function.BiConsumer;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.event.EventContext;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
//import com.nr.instrumentation.mule.api.NRBiConsumer;

@Weave(type=MatchType.Interface)
public abstract class ExecutableComponent {

	@Trace
	public CompletableFuture<ExecutionResult> execute(InputEvent inputEvent) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ExecutableComponent",getClass().getSimpleName(),"execute");
		CompletableFuture<ExecutionResult> cf = Weaver.callOriginal();
//		BiConsumer<? super ExecutionResult, ? super Throwable> action = new NRBiConsumer<ExecutionResult>(getClass().getSimpleName());
//		return cf.whenComplete(action);
		return cf;
	}
	
	@Trace
	public CompletableFuture<Event> execute(Event event) {
		
		String location = null;
		EventContext context = event.getContext();
		if(context != null) {
			ComponentLocation origLoc = context.getOriginatingLocation();
			if(origLoc != null) {
				location = origLoc.getLocation();
			}
		}
		if(location == null) {
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ExecutableComponent",getClass().getSimpleName(),"execute");
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ExecutableComponent",getClass().getSimpleName(),"execute",location);
		}
		CompletableFuture<Event> cf = Weaver.callOriginal();
//		BiConsumer<? super Event, ? super Throwable> action = new NRBiConsumer<Event>(getClass().getSimpleName());
//		return cf.whenComplete(action);
		return cf;
	}
}
