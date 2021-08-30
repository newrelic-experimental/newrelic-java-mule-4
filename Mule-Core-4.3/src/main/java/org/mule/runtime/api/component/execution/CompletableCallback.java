package org.mule.runtime.api.component.execution;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class CompletableCallback<T> {
	
	@NewField
	public Token token = null;
	
	@Trace(async=true)
	public void complete(T var1) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Weaver.callOriginal();
	}

	@Trace(async=true)
	public void error(Throwable var1) {
		NewRelic.noticeError(var1);
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Weaver.callOriginal();
	}


}
