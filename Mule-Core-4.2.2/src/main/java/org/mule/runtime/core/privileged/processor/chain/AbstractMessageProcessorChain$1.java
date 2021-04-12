package org.mule.runtime.core.privileged.processor.chain;

import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.internal.event.MuleUtils;
import org.reactivestreams.Subscription;

import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

import reactor.util.context.Context;

@Weave
class AbstractMessageProcessorChain$1 {
	
	@Trace(async=true,excludeFromTransactionTrace=true)
	public void onNext(final CoreEvent event) {
		Token token = MuleUtils.getToken(event);
		if(token != null) {
			token.link();
		}
		Weaver.callOriginal();
	}

	@Trace(async=true,excludeFromTransactionTrace=true)
	public void onError(final Throwable throwable) {
	
		Weaver.callOriginal();
	}

	@Trace(async=true,excludeFromTransactionTrace=true)
	public void onComplete() {
		Weaver.callOriginal();
	}

	public Context currentContext() {
		return Weaver.callOriginal();
	}

	public void onSubscribe(final Subscription s) {
		Weaver.callOriginal();
	}
	
}
