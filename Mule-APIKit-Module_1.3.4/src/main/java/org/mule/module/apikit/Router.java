package org.mule.module.apikit;

import org.mule.extension.http.api.HttpRequestAttributes;
import org.mule.runtime.core.api.event.CoreEvent;
import org.reactivestreams.Publisher;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.mule.apikit.NROnTerminate;

import reactor.core.publisher.Flux;

@Weave
public abstract class Router {

	public abstract String getName();
	
	@Trace(dispatcher=true)
	private Publisher<CoreEvent> doRoute(CoreEvent mainEvent, Configuration config, HttpRequestAttributes attributes) {
		Publisher<CoreEvent> result = Weaver.callOriginal();
		result = ((Flux<CoreEvent>)result).doOnTerminate(new NROnTerminate(getName()));
		return result;
	}

}
