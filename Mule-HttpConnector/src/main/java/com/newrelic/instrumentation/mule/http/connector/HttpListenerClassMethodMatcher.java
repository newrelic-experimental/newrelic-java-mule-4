package com.newrelic.instrumentation.mule.http.connector;

import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public class HttpListenerClassMethodMatcher implements ClassAndMethodMatcher {
	
	protected static final String HTTPLISTENER = "org.mule.extension.http.internal.listener.HttpListener";
	
	private HttpListenerMethodMatcher methodMatcher;
	private ExactClassMatcher classMatcher;
	
	public HttpListenerClassMethodMatcher() {
		classMatcher = new ExactClassMatcher(HTTPLISTENER);
		methodMatcher = new HttpListenerMethodMatcher();
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
