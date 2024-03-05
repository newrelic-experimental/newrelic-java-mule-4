package org.mule.runtime.extension.api.runtime.operation;

import java.util.function.Consumer;

import org.mule.runtime.api.message.Message;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;

@Weave(type=MatchType.Interface)
public abstract class FlowListener {

	@Trace(dispatcher=true)
	public abstract void onSuccess(final Consumer<Message> p0);

	@Trace(dispatcher=true)
	public abstract void onError(final Consumer<Exception> p0);

	@Trace(dispatcher=true)
	public abstract void onComplete(final Runnable p0);

}
