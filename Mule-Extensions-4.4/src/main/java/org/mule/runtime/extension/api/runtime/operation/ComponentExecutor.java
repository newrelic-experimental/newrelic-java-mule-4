package org.mule.runtime.extension.api.runtime.operation;

import org.mule.runtime.api.meta.model.ComponentModel;
import org.reactivestreams.Publisher;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class ComponentExecutor<T extends ComponentModel> {
	
	@Trace(async=true)
	public Publisher<Object> execute(final ExecutionContext<T> p0) {
		return Weaver.callOriginal();
	}
}
