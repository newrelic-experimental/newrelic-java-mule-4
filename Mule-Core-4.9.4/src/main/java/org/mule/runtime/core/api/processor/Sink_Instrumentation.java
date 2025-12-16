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

	@NewField
	public Token token;

	@Trace(excludeFromTransactionTrace=true)
	public BackPressureReason emit(CoreEvent event) {
		// Get token for async operation linking
		try {
			token = NewRelic.getAgent().getTransaction().getToken();
		} catch (Throwable t) {
			NewRelic.getAgent().getLogger().log(java.util.logging.Level.FINE, t, "Error getting token in Sink.emit");
		}

		BackPressureReason returned = Weaver.callOriginal();

		return returned;
	}

	@Trace(async=true,excludeFromTransactionTrace=true)
	public void accept(final CoreEvent event) {
		// Link and expire token for async operation
		if (token != null) {
			token.linkAndExpire();
			token = null;
		}

		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","Sink",getClass().getSimpleName(),"accept"});
		Weaver.callOriginal();
	}
}
