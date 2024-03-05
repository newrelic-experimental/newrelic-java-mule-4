package org.mule.runtime.module.extension.internal.runtime.source;

import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.internal.execution.MessageProcessContext;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.extensions_43.Utils;

@Weave
abstract class DefaultSourceCallback<T, A> {

	private String applicationName = Weaver.callOriginal();
	private MessageProcessContext messageProcessContext = Weaver.callOriginal();

	@Trace(dispatcher=true)
	public void handle(Result<T, A> result, SourceCallbackContext context) {
		if(applicationName != null) {
			Utils.addAppName(applicationName);
			NewRelic.addCustomParameter("Application-Name", applicationName != null ? applicationName : "Unnamed application");
		}
		FlowConstruct flowConstruct = messageProcessContext.getFlowConstruct();
		String flowName = flowConstruct.getName();
		if(flowName != null && !flowName.isEmpty()) {
			NewRelic.addCustomParameter("Flow-Name",flowName);
			NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "Flows", flowName);
		}

		String flowRep = flowConstruct.getRepresentation();
		if(flowRep != null && !flowRep.isEmpty()) {
			NewRelic.addCustomParameter("Flow-Representation", flowConstruct.getRepresentation());
		}
		Weaver.callOriginal();
	}
}
