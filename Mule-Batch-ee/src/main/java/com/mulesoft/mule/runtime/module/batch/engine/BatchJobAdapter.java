package com.mulesoft.mule.runtime.module.batch.engine;

import org.mule.runtime.core.api.event.CoreEvent;

import com.mulesoft.mule.runtime.module.batch.api.BatchJobInstance;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.batch.NRRunnable;

@Weave(type=MatchType.Interface)
public abstract class BatchJobAdapter {

	@Trace(dispatcher=true)
	public BatchJobInstance execute(CoreEvent muleEvent) {
		return Weaver.callOriginal();
	}
	
	public void submitWork(Runnable before, Runnable work, Runnable after) {
		Token t = NewRelic.getAgent().getTransaction().getToken();
		if(t != null && t.isActive()) {
			if(before != null) {
				NRRunnable beforeRunnable = new NRRunnable(before, t, "Before-Work");
				before = beforeRunnable;
			}
			if(work != null) {
				NRRunnable workRunnable = new NRRunnable(before, t, "Work");
				work = workRunnable;
			}
			if(after != null) {
				NRRunnable afterRunnable = new NRRunnable(before, t, "After-Work");
				after = afterRunnable;
			}
		}
		Weaver.callOriginal();
	}
}
