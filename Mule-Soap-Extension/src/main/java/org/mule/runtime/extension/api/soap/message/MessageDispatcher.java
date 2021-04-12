package org.mule.runtime.extension.api.soap.message;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class MessageDispatcher {

	
	@Trace
	public DispatchingResponse dispatch(DispatchingRequest request) {
		DispatchingResponse resp = Weaver.callOriginal();
		return resp;
	}
}
