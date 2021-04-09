package com.nr.instrumentation.mule.http;

import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.NameMethodMatcher;

public class MuleHttpMatcher implements ClassAndMethodMatcher {
	
	private ClassMatcher classMatcher = new ExactClassMatcher("org.mule.service.http.impl.service.server.grizzly.GrizzlyServer");
	private MethodMatcher methodMatcher = new NameMethodMatcher("addRequestHandler");

	@Override
	public ClassMatcher getClassMatcher() {
		return classMatcher;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return methodMatcher;
	}

}
