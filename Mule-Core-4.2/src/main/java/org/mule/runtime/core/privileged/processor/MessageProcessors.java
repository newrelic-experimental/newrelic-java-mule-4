package org.mule.runtime.core.privileged.processor;

import java.util.Optional;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.exception.FlowExceptionHandler;
import org.mule.runtime.core.api.processor.ReactiveProcessor;
import org.mule.runtime.core.privileged.event.BaseEventContext;
import org.reactivestreams.Publisher;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.Utils;

@Weave
public abstract class MessageProcessors {

	public static Publisher<CoreEvent> processWithChildContext(CoreEvent event, ReactiveProcessor processor, Optional<ComponentLocation> componentLocation) { 
		if(componentLocation.isPresent()) {
			ComponentLocation location = componentLocation.get();
			if(location != null) {
				String locName = Utils.getLocationName(location);
				if(locName != null && !locName.isEmpty()) {
					NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "MuleProcessors", locName);
				}
			}
		}
		
		return Weaver.callOriginal();
	}
	
	public static Publisher<CoreEvent> processWithChildContext(CoreEvent event, ReactiveProcessor processor, Optional<ComponentLocation> componentLocation, FlowExceptionHandler exceptionHandler) {
		if(componentLocation.isPresent()) {
			ComponentLocation location = componentLocation.get();
			if(location != null) {
				String locName = Utils.getLocationName(location);
				if(locName != null && !locName.isEmpty()) {
					NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "MuleProcessors", locName);
				}
			}
		}
		
		return Weaver.callOriginal();
	}
	
	public static Publisher<CoreEvent> processWithChildContext(CoreEvent event, ReactiveProcessor processor, BaseEventContext childContext) {
		ComponentLocation location = childContext.getOriginatingLocation();
		if(location != null) {
			String locName = Utils.getLocationName(location);
			if(locName != null && !locName.isEmpty()) {
				NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "MuleProcessors", locName);
			}
		}
		return Weaver.callOriginal();
	}
}
