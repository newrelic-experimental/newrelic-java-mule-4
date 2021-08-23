package org.mule.runtime.core.privileged.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.exception.FlowExceptionHandler;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.privileged.event.BaseEventContext;
import org.reactivestreams.Publisher;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.NRCoreUtils;
import com.newrelic.mule.core.Utils;

@Weave
public abstract class MessageProcessors {

	public static Publisher<CoreEvent> processWithChildContext(CoreEvent event, ReactiveProcessor processor, Optional<ComponentLocation> componentLocation) { 
		Map<String, Object> attributes = new HashMap<String, Object>();
		NRCoreUtils.recordCoreEvent("Input", event, attributes);
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.addCustomAttributes(attributes);
		if(componentLocation.isPresent()) {
			ComponentLocation location = componentLocation.get();
			if(location != null) {
				String locName = Utils.getLocationName(location);
				if(locName != null && !locName.isEmpty()) {
					traced.addCustomAttribute("Location", locName);
					NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "MuleProcessors", locName);
				}
			}
		}
		
		return Weaver.callOriginal();
	}
	
	public static Publisher<CoreEvent> processWithChildContext(CoreEvent event, ReactiveProcessor processor, Optional<ComponentLocation> componentLocation, FlowExceptionHandler exceptionHandler) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		NRCoreUtils.recordCoreEvent("Input", event, attributes);
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.addCustomAttributes(attributes);
		if(componentLocation.isPresent()) {
			ComponentLocation location = componentLocation.get();
			if(location != null) {
				String locName = Utils.getLocationName(location);
				if(locName != null && !locName.isEmpty()) {
					traced.addCustomAttribute("Location", locName);
					NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "MuleProcessors", locName);
				}
			}
		}
		
		return Weaver.callOriginal();
	}
	
	public static Publisher<CoreEvent> processWithChildContext(CoreEvent event, ReactiveProcessor processor, BaseEventContext childContext) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		NRCoreUtils.recordCoreEvent("Input", event, attributes);
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.addCustomAttributes(attributes);
		ComponentLocation location = childContext.getOriginatingLocation();
		if(location != null) {
			String locName = Utils.getLocationName(location);
			if(locName != null && !locName.isEmpty()) {
				traced.addCustomAttribute("OriginatingLocation", locName);
				NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "MuleProcessors", locName);
			}
		}
		return Weaver.callOriginal();
	}
}
