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
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
abstract class AbstractEventContext implements BaseEventContext {
	
	@NewField
	public Token token = null;
	
	public AbstractEventContext() {
		
	}
	
	public AbstractEventContext(FlowExceptionHandler exceptionHandler, int depthLevel,Optional<CompletableFuture<Void>> externalCompletion) { 
		setToken();
	}
	
	public abstract Optional<BaseEventContext> getParentContext();

	void addChildContext(final BaseEventContext childContext) {
		if(token == null) {
			setToken();
		}
		if(childContext != null && childContext instanceof AbstractEventContext) {
			AbstractEventContext childCtx = (AbstractEventContext)childContext;
			Token t = childCtx.token;
			
			if(token == null) {
				if(t != null) {
					token = t;
				}
			} else if(t == null) {
				if(token != null) {
					childCtx.token = token;
				}
			} else {
				if(!t.equals(token)) {
					childCtx.token.expire();
					childCtx.token = token;
				}
			}
		}
		Weaver.callOriginal();
	}

	@Trace(async=true,excludeFromTransactionTrace=true)
	public void success() {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		expireParent(getParentContext());
		Weaver.callOriginal();
	}
	
	private void expireParent(Optional<BaseEventContext> parent) {
		Optional<BaseEventContext> current = parent;
		while(current.isPresent()) {
			BaseEventContext parentCtx = current.get();
			if(parentCtx != null && (AbstractEventContext.class.isInstance(parentCtx))) {
				AbstractEventContext aCtx = (AbstractEventContext)parentCtx;
				if(aCtx.token != null) {
					aCtx.token.expire();
					aCtx.token = null;
				}
				
			}
			if(AbstractEventContext.class.isInstance(parentCtx)) {
				current = ((AbstractEventContext)parentCtx).getParentContext();
			} else {
				current = Optional.empty();
			}
		}
	}

	@Trace(async=true,excludeFromTransactionTrace=true)
	public void success(CoreEvent event) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		} else {
			
			EventContext ctx = event.getContext();
			if(AbstractEventContext.class.isInstance(ctx)) {
				AbstractEventContext bctx = (AbstractEventContext)ctx;
				if(bctx.token != null) {
					bctx.token.linkAndExpire();
					bctx.token = null;
				}
				expireParent(bctx.getParentContext());
			}
		}
		expireParent(getParentContext());
		Weaver.callOriginal();
	}

	@Trace(async=true,excludeFromTransactionTrace=true)
	public Publisher<Void> error(Throwable throwable) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		expireParent(getParentContext());
		NewRelic.noticeError(throwable);
		return Weaver.callOriginal();
	}

	private void setToken() {
		Token t = NewRelic.getAgent().getTransaction().getToken();
		if(t != null && t.isActive()) {
			token = t;
		}
	}

}
