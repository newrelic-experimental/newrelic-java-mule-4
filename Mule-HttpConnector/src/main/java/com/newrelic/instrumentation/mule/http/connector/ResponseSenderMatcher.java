package com.newrelic.instrumentation.mule.http.connector;

import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.NameMethodMatcher;

public class ResponseSenderMatcher implements ClassAndMethodMatcher {
	
	protected static final String RESPONSESENDER = "org.mule.extension.http.internal.listener.HttpListenerResponseSender";

	@Override
	public ClassMatcher getClassMatcher() {
		
		return new ExactClassMatcher(RESPONSESENDER);
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return new NameMethodMatcher("sendResponse");
	}

}
