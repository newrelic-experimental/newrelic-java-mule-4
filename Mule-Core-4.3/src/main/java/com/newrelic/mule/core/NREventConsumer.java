package com.newrelic.mule.core;

import java.util.function.Consumer;

import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.internal.event.MuleUtils;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

public class NREventConsumer implements Consumer<CoreEvent> {

	private static boolean isTransformed = false;
	private static boolean startTransformed = false;

	private String name = null;

	private Consumer<CoreEvent> delegate = null;

	public NREventConsumer(String n, Consumer<CoreEvent> d) {
		name = n;
		delegate = d;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}

	public NREventConsumer() {
		this(null,null);
	}

	@Override
	public void accept(CoreEvent event) {
		NRMuleHeaders headers = MuleUtils.getHeaders(event);
		if(headers != null && !headers.isEmpty()) {
			StartTransaction startTxn = new StartTransaction();
			startTxn.start(delegate,event,headers);
		} else {
			if(delegate != null) {
				delegate.accept(event);
			}
		}
	}

	private class StartTransaction {

		protected StartTransaction() {
			if(!startTransformed) {
				startTransformed = true;
				AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
			}
		}
		
		@Trace(dispatcher = true)
		protected void start(Consumer<CoreEvent> consumer, CoreEvent event,NRMuleHeaders headers) {
			HeaderUtils.acceptHeaders(headers, false);
			if(name != null) {
				NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EventConsumer",name});
			}
			if(consumer != null) {
				consumer.accept(event);
			}
		}
	}
}
