package com.newrelic.instrumentation.mule.http.connector;

import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public class HttpResponseReadyCallbackClassMethodMatcher implements ClassAndMethodMatcher {
	
	protected static final String RESPONSE_READY = "responseReady";
	protected static final String START_RESPONSE = "startResponse";
	
	ClassMatcher classMatcher = null;
	MethodMatcher methodMatcher = null;
	
	public HttpResponseReadyCallbackClassMethodMatcher() {
		classMatcher = new HttpResponseReadyCallbackClassMatcher();
		methodMatcher = new HttpResponseReadyCallbackMethodMatcher();
	}

	@Override
	public ClassMatcher getClassMatcher() {
		return classMatcher;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return methodMatcher;
	}

}
