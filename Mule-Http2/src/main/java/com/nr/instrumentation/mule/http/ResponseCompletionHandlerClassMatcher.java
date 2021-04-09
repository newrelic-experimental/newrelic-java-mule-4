package com.nr.instrumentation.mule.http;

import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;

public class ResponseCompletionHandlerClassMatcher extends ExactClassMatcher {

	public ResponseCompletionHandlerClassMatcher() {
		super("org.mule.service.http.impl.service.server.grizzly.BaseResponseCompletionHandler");
	}

	
}
