package org.mule.runtime.extension.api.runtime.operation;

import java.util.concurrent.atomic.AtomicInteger;

import org.mule.runtime.api.meta.model.ComponentModel;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.bridge.Transaction;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type = MatchType.Interface)
public abstract class CompletableComponentExecutor<M extends ComponentModel> {

	@Trace(dispatcher = true)
	public void execute(ExecutionContext<M> var1, ExecutorCallback var2) {
		// Store token on callback for async linking when response arrives on Netty thread
		if(var2 != null && var2.nrToken == null) {
			var2.nrToken = NewRelic.getAgent().getTransaction().getToken();
		}
		Weaver.callOriginal();
	}

	@Weave(type = MatchType.Interface)
	public static class ExecutorCallback {

		@NewField
		public Token nrToken;

		@Trace(async = true)
		public void complete(Object p0) {
			// Link token and set AgentBridge.activeToken so ning module's
			// onCompleted() @Trace(async=true) can find the transaction and close its segment
			com.newrelic.agent.bridge.Token bridgeToken = null;
			if(nrToken != null) {
				nrToken.link();
				Transaction tx = AgentBridge.getAgent().getTransaction(false);
				if(tx != null) {
					bridgeToken = tx.getToken();
					AgentBridge.TokenAndRefCount tokenAndRefCount = new AgentBridge.TokenAndRefCount(
						bridgeToken, AgentBridge.getAgent().getTracedMethod(), new AtomicInteger(1));
					AgentBridge.activeToken.set(tokenAndRefCount);
				}
			}
			try {
				Weaver.callOriginal();
			} finally {
				AgentBridge.activeToken.remove();
				if(bridgeToken != null) {
					bridgeToken.expire();
				}
				if(nrToken != null) {
					nrToken.expire();
					nrToken = null;
				}
			}
		}

		@Trace(async = true)
		public void error(Throwable p0) {
			com.newrelic.agent.bridge.Token bridgeToken = null;
			if(nrToken != null) {
				nrToken.link();
				Transaction tx = AgentBridge.getAgent().getTransaction(false);
				if(tx != null) {
					bridgeToken = tx.getToken();
					AgentBridge.TokenAndRefCount tokenAndRefCount = new AgentBridge.TokenAndRefCount(
						bridgeToken, AgentBridge.getAgent().getTracedMethod(), new AtomicInteger(1));
					AgentBridge.activeToken.set(tokenAndRefCount);
				}
			}
			try {
				Weaver.callOriginal();
			} finally {
				AgentBridge.activeToken.remove();
				if(bridgeToken != null) {
					bridgeToken.expire();
				}
				if(nrToken != null) {
					nrToken.expire();
					nrToken = null;
				}
			}
		}
	}
}
