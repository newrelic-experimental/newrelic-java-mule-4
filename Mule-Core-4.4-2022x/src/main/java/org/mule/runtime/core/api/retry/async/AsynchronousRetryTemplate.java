package org.mule.runtime.core.api.retry.async;

import java.util.concurrent.Executor;

import org.mule.runtime.core.api.retry.RetryCallback;
import org.mule.runtime.core.api.retry.RetryContext;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class AsynchronousRetryTemplate {

	@Trace(dispatcher=true)
	public RetryContext execute(RetryCallback callback, Executor workManager) {
		
		return Weaver.callOriginal();
	}
}
