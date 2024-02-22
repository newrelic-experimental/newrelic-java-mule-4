package org.mule.runtime.core.api.processor;

import java.util.HashMap;
import java.util.Map;

import org.mule.runtime.api.meta.NamedObject;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.internal.event.MuleUtils;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.MuleReactorUtils;
import com.newrelic.mule.core.NRCoreUtils;

@Weave(type=MatchType.Interface)
public abstract class Processor {

	@Trace(async=true)
	public CoreEvent process(CoreEvent event) {
		boolean named = false;
		if(this instanceof NamedObject) {
			String name = ((NamedObject)this).getName();
			if(name != null && !name.isEmpty()) {
				NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Processor",getClass().getSimpleName(),name,"execute");
				named = true;
				if(this instanceof Flow) {
					NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, false, "Flow", "Mule","Flow",name);
				}
			}
		}
		if(!named) {
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Processor",getClass().getSimpleName(),"execute");
		}
		Map<String, Object> attributes = new HashMap<String, Object>();
		NRCoreUtils.recordCoreEvent("Input", event, attributes);
		if(!MuleReactorUtils.initialized) {
			MuleReactorUtils.init();
		}
		Token token = MuleUtils.getToken(event);
		if(token != null) {
			token.link();
		} else {
			token = NewRelic.getAgent().getTransaction().getToken();
			MuleUtils.setToken(event, token);
		}
		CoreEvent returnedEvent = Weaver.callOriginal();
		NRCoreUtils.recordCoreEvent("Returned", returnedEvent, attributes);
		NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);
		
		Token token2 = MuleUtils.getToken(returnedEvent);
		if(token2 == null && token != null) {
			MuleUtils.setToken(returnedEvent, token);
			
		}
		
		return returnedEvent;
	}
	
}
