package org.mule.runtime.core.internal.retry.async;

import java.util.concurrent.Executor;

import org.mule.runtime.api.util.concurrent.Latch;
import org.mule.runtime.core.api.retry.RetryCallback;
import org.mule.runtime.core.api.retry.policy.RetryPolicyTemplate;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class RetryWorker {
	
	@NewField
	private Token token = null;
	
	public RetryWorker(RetryPolicyTemplate delegate, RetryCallback callback, Executor workManager, Latch startLatch) {
		Token t = NewRelic.getAgent().getTransaction().getToken();
		if(t != null && t.isActive()) {
			token = t;
		} else if(t != null) {
			t.expire();
			t = null;
		}
	}
	
	@Trace(async=true)
	public void run() {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Weaver.callOriginal();
	}

}
