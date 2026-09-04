package org.mule.runtime.module.extension.internal.runtime.source;

import java.util.Optional;

import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.internal.execution.MessageProcessContext;
import org.mule.sdk.api.runtime.operation.Result;
import org.mule.sdk.api.runtime.source.SourceCallbackContext;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.ApplicationNamePriority;
import com.nr.instrumentation.mule.extensions.ReflectiveHttpRequestWrapper;

@Weave
abstract class DefaultSourceCallback<T, A> {

	private String applicationName = Weaver.callOriginal();
	private MessageProcessContext messageProcessContext = Weaver.callOriginal();

	@Trace(dispatcher=true)
	public void handle(Result<T, A> result, SourceCallbackContext context) {
		NewRelic.addCustomParameter("IsWebAtHandleEntry", String.valueOf(NewRelic.getAgent().getTransaction().isWebTransaction()));

		boolean isHttpRequest = false;
		try {
			Optional<A> attrs = result.getAttributes();
			if (attrs.isPresent() && "org.mule.extension.http.api.HttpRequestAttributes".equals(attrs.get().getClass().getName())) {
				Transaction txn = NewRelic.getAgent().getTransaction();
				if (!txn.isWebTransaction()) {
					txn.convertToWebTransaction();
				}
				ReflectiveHttpRequestWrapper webRequest = new ReflectiveHttpRequestWrapper(attrs.get());
				txn.setWebRequest(webRequest);
				String uri = webRequest.getRequestURI();
				String method = webRequest.getMethod();
				if (uri != null && !uri.isEmpty()) {
					String txnName = (method != null && !method.isEmpty()) ? (method + " " + uri) : uri;
					txn.setTransactionName(TransactionNamePriority.REQUEST_URI, true, "Uri", txnName);
				}
				isHttpRequest = true;
			}
		} catch (Throwable t) {
			// never let web-transaction marking break the actual request
		}

		AgentBridge.getAgent().getTransaction(false).setApplicationName(ApplicationNamePriority.REQUEST_ATTRIBUTE, applicationName);
		String appNameOnly = applicationName;
		if (appNameOnly != null) {
			int lastDot = appNameOnly.lastIndexOf('.');
			if (lastDot >= 0 && lastDot < appNameOnly.length() - 1) {
				appNameOnly = appNameOnly.substring(lastDot + 1);
			}
		}
		NewRelic.addCustomParameter("Application-Name", appNameOnly != null ? appNameOnly : "Unnamed application");
		FlowConstruct flowConstruct = messageProcessContext.getFlowConstruct();
		String flowName = flowConstruct.getName();
		if(flowName != null && !flowName.isEmpty()) {
			NewRelic.addCustomParameter("Flow-Name",flowName);
			if (!isHttpRequest) {
				NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "Flows", flowName);
			}
		}

		NewRelic.addCustomParameter("Flow-Representation", flowConstruct.getRepresentation());
		Weaver.callOriginal();
	}
}
