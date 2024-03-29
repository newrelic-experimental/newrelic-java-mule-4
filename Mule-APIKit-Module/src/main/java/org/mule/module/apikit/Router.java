package org.mule.module.apikit;

import java.util.concurrent.CompletableFuture;

import org.mule.extension.http.api.HttpRequestAttributes;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.core.api.event.CoreEvent;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.apikit.NRBiConsumer;

@Weave
public abstract class Router {

	public abstract String getName();
	
	@Trace(dispatcher=true)
	private CompletableFuture<Event> doRoute(CoreEvent event, Configuration config,CoreEvent.Builder eventBuilder, HttpRequestAttributes attributes) {
		CompletableFuture<Event> result = Weaver.callOriginal();
		return result.whenComplete(new NRBiConsumer<>(getName()));
	}
	
}
