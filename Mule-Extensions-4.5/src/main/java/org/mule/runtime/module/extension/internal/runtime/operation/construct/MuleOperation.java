package org.mule.runtime.module.extension.internal.runtime.operation.construct;

import java.util.concurrent.CompletableFuture;

import org.mule.runtime.api.component.execution.ExecutionResult;
import org.mule.runtime.api.component.execution.InputEvent;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.meta.model.operation.OperationModel;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.extensions_45.NRBiConsumer;

@Weave
public class MuleOperation {
	
	private final OperationModel operationModel = Weaver.callOriginal();

	@Trace
	public CompletableFuture<ExecutionResult> execute(InputEvent inputEvent) {
		CompletableFuture<ExecutionResult> result = Weaver.callOriginal();
		String opname = operationModel.getName();
		String name = opname != null && !opname.isEmpty() ? opname : "UnknownOperation";
		NRBiConsumer<ExecutionResult> wrapper = new NRBiConsumer<>(name);
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","MuleOperation", "execute",name);
		return result.whenComplete(wrapper);
	}

	@Trace
	public CompletableFuture<Event> execute(Event event) {
		CompletableFuture<Event> result = Weaver.callOriginal();
		String opname = operationModel.getName();
		String name = opname != null && !opname.isEmpty() ? opname : "UnknownOperation";
		NRBiConsumer<Event> wrapper = new NRBiConsumer<>(name);
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","MuleOperation", "execute",name);
		
		return result.whenComplete(wrapper);
	}
}
