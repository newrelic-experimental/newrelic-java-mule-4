package org.mule.service.scheduler.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.scheduler.NRCallable;
import com.nr.instrumentation.mule.scheduler.NRMuleHeaders;
import com.nr.instrumentation.mule.scheduler.NRRunnable;

@Weave(type=MatchType.BaseClass)
public abstract class DefaultScheduler {

	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		if(!(command instanceof NRRunnable)) {
			NRMuleHeaders headers = new NRMuleHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
			if(!headers.isEmpty()) {
				NRRunnable wrapper = new NRRunnable(command,headers);
				command = wrapper;
			}
		}
		return Weaver.callOriginal();
	}

	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		if(!(callable instanceof NRCallable)) {
			NRMuleHeaders headers = new NRMuleHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
			if(!headers.isEmpty()) {
				NRCallable<V> wrapper = new NRCallable<V>(callable, headers);
				callable = wrapper;
			}
		}
		return Weaver.callOriginal();
	}
}
