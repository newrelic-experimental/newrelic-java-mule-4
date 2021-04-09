package org.mule.module.apikit;

import org.mule.extension.http.api.HttpRequestAttributes;
import org.mule.runtime.core.api.event.CoreEvent;
import org.reactivestreams.Publisher;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class Router {

	public abstract String getName();
	
	@Trace(dispatcher=true)
	private Publisher<CoreEvent> doRoute(CoreEvent event, Configuration config,CoreEvent.Builder eventBuilder, HttpRequestAttributes attributes) {
		return Weaver.callOriginal();
	}
	
}
