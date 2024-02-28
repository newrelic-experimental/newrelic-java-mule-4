package org.mule.runtime.module.extension.internal.runtime.operation;

import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.extension.api.runtime.operation.CompletableComponentExecutor.ExecutorCallback;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

import reactor.util.context.Context;

@Weave
public abstract class ComponentMessageProcessor {

	@Trace(dispatcher = true)
	private void onEvent(CoreEvent event, ExecutorCallback executorCallback, Context ctx) {
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher = true)
	private void onEventSynchronous(CoreEvent event, ExecutorCallback executorCallback,Context ctx) {
		Weaver.callOriginal();		
	}
}
