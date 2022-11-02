package com.newrelic.instrumentation.mule.http.connector;

import java.util.Collections;

import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public class HttpRequesterClassMethodMatcher implements ClassAndMethodMatcher {
	
	protected static final String HTTPREQUESTER = "org.mule.extension.http.internal.request.HttpRequester";

	@Override
	public ClassMatcher getClassMatcher() {
		return new ExactClassMatcher(HTTPREQUESTER);
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return new ExactMethodMatcher("doRequest", Collections.emptyList());
	}

}
