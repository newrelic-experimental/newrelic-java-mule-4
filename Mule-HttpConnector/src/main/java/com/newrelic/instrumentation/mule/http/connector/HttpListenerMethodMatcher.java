package com.newrelic.instrumentation.mule.http.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public class HttpListenerMethodMatcher implements MethodMatcher {
	
	List<String> methods = new ArrayList<String>();
	
	public HttpListenerMethodMatcher() {
		methods.add("onError");
		methods.add("onSuccess");
		methods.add("onStart");
	}

	@Override
	public boolean matches(int access, String name, String desc, Set<String> annotations) {
		return methods.contains(name);
	}

	@Override
	public Method[] getExactMethods() {
		// TODO Auto-generated method stub
		return null;
	}

}
