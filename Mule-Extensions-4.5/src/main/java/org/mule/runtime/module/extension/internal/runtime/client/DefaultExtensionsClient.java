package org.mule.runtime.module.extension.internal.runtime.client;

import java.util.concurrent.CompletableFuture;

import org.mule.runtime.extension.api.client.OperationParameters;
import org.mule.runtime.extension.api.runtime.operation.Result;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class DefaultExtensionsClient {

	@Trace(dispatcher=true)
	public <T, A> Result<T, A> execute(String extension, String operation, OperationParameters params) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","DefaultExtensionsClient","execute",extension,operation);
		return Weaver.callOriginal();
	}
	
	@Trace(dispatcher=true)
	public <T, A> CompletableFuture<Result<T, A>> executeAsync(String extension, String operation,OperationParameters parameters) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","DefaultExtensionsClient","executeAsync",extension,operation);
		return Weaver.callOriginal();
	}
}
