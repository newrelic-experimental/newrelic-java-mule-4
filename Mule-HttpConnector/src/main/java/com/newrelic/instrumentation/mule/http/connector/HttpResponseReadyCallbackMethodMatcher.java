package com.newrelic.instrumentation.mule.http.connector;

import java.util.Set;
import java.util.logging.Level;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.NameMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.api.agent.NewRelic;

public class HttpResponseReadyCallbackMethodMatcher implements MethodMatcher {
	
	private MethodMatcher actual = null;
	
	public HttpResponseReadyCallbackMethodMatcher() {
		actual = OrMethodMatcher.getMethodMatcher(new NameMethodMatcher(HttpResponseReadyCallbackClassMethodMatcher.RESPONSE_READY), 
				new NameMethodMatcher(HttpResponseReadyCallbackClassMethodMatcher.START_RESPONSE));
	}

	@Override
	public boolean matches(int access, String name, String desc, Set<String> annotations) {
		boolean b = actual.matches(access, name, desc, annotations);
		if(b) {
			NewRelic.getAgent().getLogger().log(Level.FINE, "Found  HttpResponseReadyCallback method match, {0}, {1}" , name,desc);
		}
		return b;
	}

	@Override
	public Method[] getExactMethods() {
		
		return actual.getExactMethods();
	}

}
