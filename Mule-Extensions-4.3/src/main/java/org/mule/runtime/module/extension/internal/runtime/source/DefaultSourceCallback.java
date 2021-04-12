package org.mule.runtime.module.extension.internal.runtime.source;

import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.internal.execution.MessageProcessContext;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.ApplicationNamePriority;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@SuppressWarnings("deprecation")
@Weave
abstract class DefaultSourceCallback<T, A> {

	private String applicationName = Weaver.callOriginal();
	private MuleContext muleContext = Weaver.callOriginal();
	private MessageProcessContext messageProcessContext = Weaver.callOriginal();

	@Trace(dispatcher=true)
	public void handle(Result<T, A> result, SourceCallbackContext context) {
		AgentBridge.getAgent().getTransaction(false).setApplicationName(ApplicationNamePriority.REQUEST_ATTRIBUTE, applicationName);
		NewRelic.addCustomParameter("Application-Name", applicationName != null ? applicationName : "Unnamed application");
		NewRelic.addCustomParameter("MuleContext-ID", muleContext.getId());
		FlowConstruct flowConstruct = messageProcessContext.getFlowConstruct();
		String flowName = flowConstruct.getName();
		if(flowName != null && !flowName.isEmpty()) {
			NewRelic.addCustomParameter("Flow-Name",flowName);
			NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "Flows", flowName);
		}

		NewRelic.addCustomParameter("Flow-Representation", flowConstruct.getRepresentation());
		Weaver.callOriginal();
	}
}
