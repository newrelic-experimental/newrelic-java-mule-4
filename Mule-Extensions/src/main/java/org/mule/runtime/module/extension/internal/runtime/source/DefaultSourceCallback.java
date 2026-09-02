package org.mule.runtime.module.extension.internal.runtime.source;

import java.util.Optional;
import java.util.function.Supplier;

import org.mule.runtime.core.api.source.MessageSource;
import org.mule.runtime.core.privileged.execution.MessageProcessContext;
import org.mule.runtime.extension.api.runtime.config.ConfigurationInstance;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.extensions.ReflectiveHttpRequestWrapper;
import com.nr.instrumentation.mule.extensions.Utils;

@Weave
abstract class DefaultSourceCallback<T, A> {

	private Supplier<MessageProcessContext> processContextSupplier = Weaver.callOriginal();

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

		if(processContextSupplier != null) {
			MessageProcessContext msgCtx = processContextSupplier.get();
			if(msgCtx != null) {
				MessageSource msgSource = msgCtx.getMessageSource();
				String locName = Utils.getMessageLocation(msgSource);
				if(locName != null && !locName.isEmpty()) {
					if(locName.endsWith("/source")) {
						locName = locName.replace("/source", "");
					}
					if (!isHttpRequest) {
						NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "Flows", locName);
					}
				}

			}
		}
		NewRelic.addCustomParameter("Owning-Source-Name", getOwningSourceName() != null ? getOwningSourceName() : "Unnamed");
		NewRelic.addCustomParameter("Owning-Extension-Name", getOwningExtensionName() != null ? getOwningExtensionName() : "Unnamed");
		NewRelic.addCustomParameter("Configuration-Instance-Name", getConfigurationInstance().getName());
		Weaver.callOriginal();
	}

	public abstract String getOwningSourceName();

	public abstract String getOwningExtensionName();

	public abstract ConfigurationInstance getConfigurationInstance();
}
