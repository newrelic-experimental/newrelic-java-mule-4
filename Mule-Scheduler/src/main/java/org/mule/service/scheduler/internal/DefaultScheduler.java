package org.mule.service.scheduler.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.scheduler.NRCallable;
import com.nr.instrumentation.mule.scheduler.NRRunnable;

@Weave(type=MatchType.BaseClass)
public abstract class DefaultScheduler {

	@Trace(dispatcher = true)
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		if(!(command instanceof NRRunnable)) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			if(token != null && token.isActive()) {
				NRRunnable wrapper = new NRRunnable(command,token);
				command = wrapper;
			} else if(token != null) {
				token.expire();
				token = null;
			}
		}
		return Weaver.callOriginal();
	}

	@Trace(dispatcher = true)
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		if(!(callable instanceof NRCallable)) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			if(token != null && token.isActive()) {
				NRCallable<V> wrapper = new NRCallable<V>(callable, token);
				callable = wrapper;
			} else if(token != null) {
				token.expire();
				token = null;
			}
		}
		return Weaver.callOriginal();
	}
	
}
