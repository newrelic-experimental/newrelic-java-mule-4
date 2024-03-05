package org.mule.runtime.extension.api.runtime.process;

import org.mule.runtime.extension.api.runtime.operation.Result;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;

@Weave(type=MatchType.Interface)
public abstract class CompletionCallback<T, A> {

	@Trace(dispatcher=true)
	public abstract void success(final Result<T, A> p0);

	@Trace(dispatcher=true)
	public abstract void error(final Throwable p0);

}
