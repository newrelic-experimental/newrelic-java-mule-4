package org.mule.runtime.core.internal.processor;

import java.lang.reflect.Method;

import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.internal.event.MuleUtils;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class InvokerMessageProcessor {
	
	protected Method method = Weaver.callOriginal();

	@Trace(async=true)
	public CoreEvent process(final CoreEvent event) {
		Token token = MuleUtils.getToken(event);
		if(token != null) {
			token.link();
		}
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","InvokerMessageProcessor","process",method.getDeclaringClass().getName(),method.getName()});
		return Weaver.callOriginal();
	}
}
