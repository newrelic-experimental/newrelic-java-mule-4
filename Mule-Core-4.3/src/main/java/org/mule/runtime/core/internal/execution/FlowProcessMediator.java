package org.mule.runtime.core.internal.execution;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class FlowProcessMediator {

	@Trace
	public void process(FlowProcessTemplate template,MessageProcessContext messageProcessContext,PhaseResultNotifier phaseResultNotifier) {
		
		Weaver.callOriginal();
	}
}
