package org.mule.runtime.module.extension.internal.runtime.source.legacy;

import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(originalName = "org.mule.runtime.module.extension.internal.runtime.source.legacy.LegacySourceCallbackAdapter")
public abstract class LegacySourceCallbackAdapter_Instrumentation<T, A> {

	public void handle(Result<T, A> result, SourceCallbackContext context) {
		NewRelic.addCustomParameter("IsWebAtLegacyAdapterEntry",
				String.valueOf(NewRelic.getAgent().getTransaction().isWebTransaction()));
		Weaver.callOriginal();
	}

}
