package org.mule.runtime.core.internal.execution;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.core.api.source.MessageSource;
import org.mule.runtime.core.privileged.execution.MessageProcessContext;
import org.mule.runtime.core.privileged.execution.MessageProcessTemplate;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class PhaseExecutionEngine {

	@Trace
	public void process(MessageProcessTemplate messageProcessTemplate, MessageProcessContext messageProcessContext) {
		String location = "Unknown";
		MessageSource source = messageProcessContext.getMessageSource();
		if(source != null) {
			ComponentLocation componentLoc = source.getLocation();
			if(componentLoc != null) {
				String temp = componentLoc.getLocation();
				if(temp != null && !temp.isEmpty()) {
					location = temp;
					if(location.endsWith("/source")) {
						location = location.replace("/source", "").trim();
					}
				}
			}
		}
		if(!location.equals("Unknown")) {
			NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "Flow", location);
		}
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","PhaseExecutionEngine","process",location);
		Weaver.callOriginal();
	}
	
	@Weave
	public static class InternalPhaseExecutionEngine {

		private final MessageProcessContext messageProcessContext = Weaver.callOriginal();
		
		public InternalPhaseExecutionEngine(final MessageProcessTemplate messageProcessTemplate,
				final MessageProcessContext messageProcessContext) {
			
		}
		
		@NewField
		private String name = null;

		@NewField
		private Token token = null;
		
		@Trace
		public void process() {
			if(messageProcessContext != null) {
				MessageSource source = messageProcessContext.getMessageSource();
				if(source != null) {
					ComponentLocation location = source.getLocation();
					if(location != null) {
						String tmp = location.getLocation();
						if(tmp != null && !tmp.isEmpty()) {
							token = NewRelic.getAgent().getTransaction().getToken();
							name = "Phase-"+tmp;
						}
					}
				}
			}
			Weaver.callOriginal();
		}

		@Trace(async=true)
		private void processEndPhase() {
			if(token != null) {
				token.linkAndExpire();
				token = null;
			}
			if(name != null) {
				NewRelic.getAgent().getTracedMethod().setMetricName("Custom","PhaseExecution","EndPhase",name);
			}
			Weaver.callOriginal();
		}
	}
}
