package com.nr.instrumentation.mule.scheduler;

import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracing.SpanProxy;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.TransportType;

public class HeaderUtils {

	public static void acceptHeaders(NRMuleHeaders headers) {
		if(headers != null && !headers.isEmpty()) {
			Transaction tx = Transaction.getTransaction(false);
			if(tx != null) {
				SpanProxy spanProxy = tx.getSpanProxy();
				if(spanProxy.getOutboundDistributedTracePayload() == null) {
					NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, headers);
					return;
				}
			}
			NewRelic.getAgent().getTransaction().ignore();
		}		
	}

}
