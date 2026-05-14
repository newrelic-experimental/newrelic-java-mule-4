package org.mule.runtime.api.component.execution;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.HeaderUtils;
import com.newrelic.mule.core.NRMuleHeaders;

@Weave(type=MatchType.Interface)
public abstract class CompletableCallback<T> {

	@NewField
	public NRMuleHeaders headers;

	@NewField
	public Token token;

	public void complete(T var1) {
		// Token linking handled by executor module (wraps ForkJoinPool submissions)
		// If token present from executor wrapping, link it here as backup
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Weaver.callOriginal();
		try {
			HeaderUtils.acceptHeaders(headers);
			headers = null;
		} catch (Exception e) { }
	}

	public void error(Throwable var1) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Weaver.callOriginal();
		try {
			NewRelic.noticeError(var1);
			HeaderUtils.acceptHeaders(headers);
			headers = null;
		} catch (Exception e) { }
	}

}
