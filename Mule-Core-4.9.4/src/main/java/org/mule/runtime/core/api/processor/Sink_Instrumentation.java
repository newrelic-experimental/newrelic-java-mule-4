package org.mule.runtime.core.api.processor;

import org.mule.runtime.core.api.construct.BackPressureReason;
import org.mule.runtime.core.api.event.CoreEvent;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface, originalName="org.mule.runtime.core.api.processor.Sink")
public abstract class Sink_Instrumentation {

	// DISABLED: Sink.emit() creates tokens on EVERY Sink instance but accept() doesn't
	// fire for all of them (async HTTP request Sinks) — causes 3 leaked tokens per request.
	// Token linking handled by ExecutorCallback.nrToken instead.
	// @NewField
	// public Token token;

	// @Trace(excludeFromTransactionTrace=true)
	// public BackPressureReason emit(CoreEvent event) { ... }

	// @Trace(async=true,excludeFromTransactionTrace=true)
	// public void accept(final CoreEvent event) { ... }
}
