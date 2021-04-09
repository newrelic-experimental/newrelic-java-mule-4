package com.mulesoft.mule.runtime.module.batch.engine;

import com.mulesoft.mule.runtime.module.batch.api.record.Record;
import com.mulesoft.mule.runtime.module.batch.engine.transaction.BatchTransactionContext;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class DefaultBatchStep {

	public abstract String getName();
	
	public Record onRecord(Record record, BatchTransactionContext ctx) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","DefaultBatchStep",getName(),"onRecord");
		return Weaver.callOriginal();
	}
}
