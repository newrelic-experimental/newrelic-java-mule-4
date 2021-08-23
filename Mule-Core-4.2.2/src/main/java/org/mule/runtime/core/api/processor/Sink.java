package org.mule.runtime.core.api.processor;

import java.util.HashMap;
import java.util.Map;

import org.mule.runtime.core.api.construct.BackPressureReason;
import org.mule.runtime.core.api.event.CoreEvent;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.NRCoreUtils;

@Weave(type=MatchType.Interface)
public abstract class Sink {

	@Trace
	public BackPressureReason emit(CoreEvent event) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		NRCoreUtils.recordCoreEvent("Input", event, attributes);
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.addCustomAttributes(attributes);
		traced.setMetricName(new String[] {"Custom","Sink",getClass().getSimpleName(),"emit"});
		BackPressureReason returned = Weaver.callOriginal();
		
		return returned;
	}
	
	@Trace(async=true,excludeFromTransactionTrace=true)
	public void accept(final CoreEvent event) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","Sink",getClass().getSimpleName(),"accept"});
		Weaver.callOriginal();
	}
}
