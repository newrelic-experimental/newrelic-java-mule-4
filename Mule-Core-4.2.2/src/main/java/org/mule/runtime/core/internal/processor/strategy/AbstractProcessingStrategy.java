package org.mule.runtime.core.internal.processor.strategy;

import java.util.function.Consumer;

import org.mule.runtime.core.api.event.CoreEvent;

import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core.NREventConsumer;

@Weave(type=MatchType.BaseClass)
public class AbstractProcessingStrategy {

	protected Consumer<CoreEvent> createOnEventConsumer() {
		Consumer<CoreEvent> consumer = Weaver.callOriginal();
		if(NREventConsumer.class.isInstance(consumer)) {
			return consumer;
		} else {
			NREventConsumer wrapper = new NREventConsumer("ProcessingStrategy-Create");
			return consumer.andThen(wrapper);
		}
	}
}
