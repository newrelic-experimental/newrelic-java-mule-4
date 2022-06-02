package org.mule.runtime.http.api.server;

import org.mule.runtime.http.api.domain.message.request.HttpRequest;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
class DefaultPathAndMethodRequestMatcher {

	private final String path = Weaver.callOriginal();
	
	@Trace(excludeFromTransactionTrace = true)
	public boolean matches(HttpRequest request) {
		boolean result = Weaver.callOriginal();
		if(result) {
			String pathToReport = path != null && !path.isEmpty() ? path : "RootPath";
			NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, true, "Grizzly", pathToReport);
		}
		return result;
	}
}
