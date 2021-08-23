package org.mule.runtime.core.internal.execution;

import java.util.HashMap;
import java.util.Map;

import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.internal.event.MuleUtils;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.NRCoreUtils;

@Weave(type=MatchType.Interface)
public abstract class ModuleFlowProcessingPhaseTemplate {

	@Trace(async=true)
	public CoreEvent routeEvent(CoreEvent event) {

		Map<String, Object> attributes = new HashMap<String, Object>();
		NRCoreUtils.recordCoreEvent("Input", event, attributes);
		Token token = MuleUtils.getToken(event);
		if(token != null) {
			token.link();
		}
		CoreEvent returnedEvent = Weaver.callOriginal();
		NRCoreUtils.recordCoreEvent("Returned", returnedEvent, attributes);
		NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);

		MuleUtils.setToken(returnedEvent, token);
		
		return returnedEvent;
	}
}
