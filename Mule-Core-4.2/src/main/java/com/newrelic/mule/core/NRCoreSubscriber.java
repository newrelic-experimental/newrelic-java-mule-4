package com.newrelic.mule.core;


import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.internal.event.MuleUtils;
import org.reactivestreams.Subscription;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

public class NRCoreSubscriber<T> implements CoreSubscriber<T> {

	private CoreSubscriber<T> coreSubscriber;
	private static boolean isTransformed = false;

	public NRCoreSubscriber(CoreSubscriber<T> c, String rType) {
		coreSubscriber= c;
		if(!isTransformed) {
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
			isTransformed = true;
		}
	}

	@Override
	@Trace(async=true,excludeFromTransactionTrace=true)
	public void onNext(T t) {
		if(t instanceof CoreEvent) {
			CoreEvent e = (CoreEvent)t;
			Token token = MuleUtils.getToken(e);
			if(token != null) {
				token.link();
//			} else {
//				token = NewRelic.getAgent().getTransaction().getToken();
//				if(token != null && token.isActive()) {
//					MuleUtils.setToken(e, token);
//				} else {
//					if(token != null) {
//						token.expire();
//						token = null;
//					}
//				}
			}
		}
		coreSubscriber.onNext(t);
	}

	@Override
	@Trace(async=true,excludeFromTransactionTrace=true)
	public void onError(Throwable t) {
		NewRelic.noticeError(t);
		coreSubscriber.onError(t);
	}

	@Override
	@Trace(async=true,excludeFromTransactionTrace=true)
	public void onComplete() {
		coreSubscriber.onComplete();
	}

	@Override
	public void onSubscribe(Subscription s) {
		coreSubscriber.onSubscribe(s);
	}

	@Override
	public Context currentContext() {
		return coreSubscriber.currentContext();
	}

}
