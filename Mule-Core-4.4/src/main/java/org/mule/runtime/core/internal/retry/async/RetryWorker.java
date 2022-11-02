package org.mule.runtime.core.internal.retry.async;

import java.util.concurrent.Executor;

import org.mule.runtime.api.util.concurrent.Latch;
import org.mule.runtime.core.api.retry.RetryCallback;
import org.mule.runtime.core.api.retry.policy.RetryPolicyTemplate;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.HeaderUtils;
import com.newrelic.mule.core.NRMuleHeaders;

@Weave
public abstract class RetryWorker {
	
	@NewField
	private NRMuleHeaders headers = null;
	
	public RetryWorker(RetryPolicyTemplate delegate, RetryCallback callback, Executor workManager, Latch startLatch) {
		headers = new NRMuleHeaders();
		NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
	}
	
	@Trace(dispatcher=true)
	public void run() {
		HeaderUtils.acceptHeaders(headers);
		Weaver.callOriginal();
	}

}
