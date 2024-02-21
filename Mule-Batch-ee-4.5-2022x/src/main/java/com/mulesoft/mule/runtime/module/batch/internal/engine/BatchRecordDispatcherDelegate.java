package com.mulesoft.mule.runtime.module.batch.internal.engine;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class BatchRecordDispatcherDelegate {
	
	@Trace(dispatcher = true)
	public void run() {
		Weaver.callOriginal();
	}

}
