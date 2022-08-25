package org.mule.service.http.impl.service.server.grizzly;

import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.FilterChainEvent;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.http.HttpContent;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.mule.service.http.impl.service.server.RequestHandlerProvider;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.http.InboundRequest;
 
@SuppressWarnings("unused")
@Weave
public abstract class GrizzlyRequestDispatcherFilter {
	
	private final RequestHandlerProvider requestHandlerProvider = Weaver.callOriginal();
	
	GrizzlyRequestDispatcherFilter(RequestHandlerProvider requestHandlerProvider) {
	}

	@Trace(dispatcher=true)
	public NextAction handleRead(final FilterChainContext ctx) {
		Transaction txn = NewRelic.getAgent().getTransaction();
		if(!txn.isWebTransaction()) {
			txn.convertToWebTransaction();
		}
		if(ctx.getMessage() instanceof HttpContent) {
			HttpContent httpContent = (HttpContent) ctx.getMessage();
			HttpRequestPacket request = (HttpRequestPacket) httpContent.getHttpHeader();
			if(request != null) {
				String requestURI = request.getRequestURI();
				if(requestURI != null) {
					if(requestURI.isEmpty()) {
						requestURI = "Root";
					}
					NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "Grizzly", requestURI);
				}
				InboundRequest wrapper = new InboundRequest(request);
				txn.setWebRequest(wrapper);
			}
		}
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","GrizzlyRequestDispatcherFilter","handleRead",ctx.getMessage().getClass().getSimpleName()});
		return Weaver.callOriginal();
	}
	
	@Trace(dispatcher=true)
	public NextAction handleEvent(FilterChainContext ctx, FilterChainEvent event) {
		String msgClass = "UnknownMessageClass";
		String eventClass = "UnknownEventClass";
		
		if(ctx != null) {
			Object msg = ctx.getMessage();
			if(msg != null) {
				msgClass = msg.getClass().getSimpleName();
			}
		}
		if(event != null) {
			eventClass = event.getClass().getSimpleName();
		}
		
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","GrizzlyRequestDispatcherFilter","handleEvent",msgClass,eventClass});
		
		return Weaver.callOriginal();
	}
	
}
