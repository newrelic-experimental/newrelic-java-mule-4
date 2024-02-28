package org.mule.runtime.module.extension.internal.runtime.operation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.privileged.processor.chain.MessageProcessorChain;
import org.mule.runtime.extension.api.runtime.operation.Result;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@SuppressWarnings("rawtypes")
@Weave
abstract class ChainExecutor {
	
	@NewField
	private Token token = null;
	
	ChainExecutor(MessageProcessorChain chain, CoreEvent originalEvent) {
		
	}

	@Trace
	public void execute(CoreEvent event, Consumer<Result> successHandler, BiConsumer<Throwable, Result> errorHandler) {
		Token t = NewRelic.getAgent().getTransaction().getToken();
		if(t != null && t.isActive()) {
			token = t;
		} else if(t != null) {
			t.expire();
			t = null;
		}
		Weaver.callOriginal();
	}
	
	@Trace(async = true)
	private void handleSuccess(CoreEvent childEvent, Consumer<Result> successHandler, BiConsumer<Throwable, Result> errorHandler) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Weaver.callOriginal();
	}
	
	@Trace(async = true)
	private CoreEvent handleError(Throwable error, CoreEvent childEvent, BiConsumer<Throwable, Result> errorHandler) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		return Weaver.callOriginal();
	}
}
