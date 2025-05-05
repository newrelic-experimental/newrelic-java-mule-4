package org.mule.runtime.module.extension.internal.runtime.source;

import java.util.function.Supplier;

import org.mule.runtime.core.api.source.MessageSource;
import org.mule.runtime.core.privileged.execution.MessageProcessContext;
import org.mule.runtime.extension.api.runtime.config.ConfigurationInstance;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.extensions.Utils;

@Weave
abstract class DefaultSourceCallback<T, A> {

	private Supplier<MessageProcessContext> processContextSupplier = Weaver.callOriginal();

	@Trace(dispatcher=true)
	public void handle(Result<T, A> result, SourceCallbackContext context) {

		if(processContextSupplier != null) {
			MessageProcessContext msgCtx = processContextSupplier.get();
			if(msgCtx != null) {
				MessageSource msgSource = msgCtx.getMessageSource();
				String locName = Utils.getMessageLocation(msgSource);
				if(locName != null && !locName.isEmpty()) {
					if(locName.endsWith("/source")) {
						locName = locName.replace("/source", "");
					}
					NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "Flows", locName);
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
