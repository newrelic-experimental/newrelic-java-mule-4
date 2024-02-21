package com.mulesoft.mule.runtime.module.batch.internal.engine.threading;

import com.mulesoft.mule.runtime.module.batch.engine.transaction.BatchTransactionContext;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.batch.NRRunnable;

@Weave(type=MatchType.Interface)
public abstract class BatchWorkManager {

	@Trace(dispatcher=true)
	public void scheduleRecordWork(BatchTransactionContext ctx, Runnable work) {
		NRRunnable wrapper = new NRRunnable(work, NewRelic.getAgent().getTransaction().getToken());
		work = wrapper;
		Weaver.callOriginal();
	}
}
