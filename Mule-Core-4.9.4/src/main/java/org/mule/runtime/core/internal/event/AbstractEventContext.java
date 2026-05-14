package org.mule.runtime.core.internal.event;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.mule.runtime.api.event.EventContext;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.exception.FlowExceptionHandler;
import org.mule.runtime.core.privileged.event.BaseEventContext;
import org.reactivestreams.Publisher;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.WeaveAllConstructors;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.HeaderUtils;
import com.newrelic.mule.core.NRMuleHeaders;

@Weave
public abstract class AbstractEventContext implements BaseEventContext {

	@NewField
	public NRMuleHeaders headers;

	@NewField
	public Token token;

	// CONFIRMED: ANY constructor instrumentation (@WeaveAllConstructors or explicit) deadlocks Mule 4.9.x
	// Both patterns modify AbstractEventContext constructor bytecode which blocks reactive initialization
	// Headers must be initialized lazily (e.g., in addChildContext or at first use)
	//
	// @WeaveAllConstructors
	// protected AbstractEventContext() {
	// 	if(this instanceof DefaultEventContext) {
	// 		setHeaders();
	// 	}
	// }
	//
	// protected AbstractEventContext() {
	// }
	// protected AbstractEventContext(FlowExceptionHandler exceptionHandler, int depthLevel, Optional<CompletableFuture<Void>> externalCompletion) {
	// 	if(this instanceof DefaultEventContext) {
	// 		setHeaders();
	// 	}
	// }

	public abstract Optional<BaseEventContext> getParentContext();

	// PHASE 2: Re-enabled
	void addChildContext(final BaseEventContext childContext) {
		if(childContext != null && childContext instanceof AbstractEventContext) {
			NRMuleHeaders childHeaders = MuleUtils.getHeaders(childContext);
			if(childHeaders == null || childHeaders.isEmpty()) {
				if(headers != null) {
					if(headers.isEmpty()) {
						NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
						if(!headers.isEmpty()) {
							MuleUtils.setHeaders(childContext, headers);
						}
					}
				}
			}
		}
		Weaver.callOriginal();
	}

	// PHASE 2: Re-enabled with token linking
	public void success() {
		// Link token from routeEventAsync to this completion thread
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Weaver.callOriginal();
		try {
			if(headers != null && !headers.isEmpty()) {
				HeaderUtils.acceptHeaders(headers);
				headers.clear();
				headers = null;
			}
		} catch (Exception e) { }
	}

	public void success(CoreEvent event) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Weaver.callOriginal();
		try {
			if(headers != null && !headers.isEmpty()) {
				HeaderUtils.acceptHeaders(headers);
				headers.clear();
				headers = null;
			} else if(event != null) {
				EventContext ctx = event.getContext();
				if(ctx instanceof AbstractEventContext) {
					AbstractEventContext bctx = (AbstractEventContext) ctx;
					if(bctx.headers != null && !bctx.headers.isEmpty()) {
						HeaderUtils.acceptHeaders(bctx.headers);
						bctx.headers.clear();
						bctx.headers = null;
					}
				}
			}
		} catch (Exception e) { }
	}

	public Publisher<Void> error(Throwable throwable) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Publisher<Void> result = Weaver.callOriginal();
		try {
			NewRelic.noticeError(throwable);
			if(headers != null && !headers.isEmpty()) {
				HeaderUtils.acceptHeaders(headers);
				headers.clear();
				headers = null;
			}
		} catch (Exception e) { }
		return result;
	}

	private void setHeaders() {
		if(headers == null) {
			headers = MuleUtils.getHeaders(getRootContext());
			if(headers == null || headers.isEmpty()) {
				try {
					BaseEventContext root = getRootContext();
					if (root != null) {
						MuleUtils.setHeaders(root);
					}
				} catch (NullPointerException e) {
				}
			}
		}
	}

}
