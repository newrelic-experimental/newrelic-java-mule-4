package org.mule.runtime.extension.api.runtime.process;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;

@Weave(type=MatchType.Interface)
public abstract class VoidCompletionCallback {

	@Trace(dispatcher=true)
	public abstract void success();

	@Trace(dispatcher=true)
	public abstract void error(final Throwable p0);

}
