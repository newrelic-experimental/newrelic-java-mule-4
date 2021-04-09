package com.nr.instrumentation.mule.http;

import java.util.Set;
import java.util.logging.Level;

import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public class ResponseCompletionHandlerMethodMatcher implements MethodMatcher {

	static final String methodDescription = "(Lorg/glassfish/grizzly/http/HttpRequestPacket;Lorg/mule/runtime/http/api/domain/message/response/HttpResponse;)Lorg/glassfish/grizzly/http/HttpResponsePacket;";

	private ExactMethodMatcher methodMatcher = new ExactMethodMatcher("buildHttpResponsePacket", methodDescription);
//	private MethodMatcher methodMatcher = new NameMethodMatcher("buildHttpResponsePacket");
	
	public ResponseCompletionHandlerMethodMatcher() {
	}

	@Override
	public boolean matches(int access, String name, String desc, Set<String> annotations)  {
		boolean matches = methodMatcher.matches(access, name, desc, annotations);
		Agent.LOG.log(Level.FINE, "call to ResponseCompletionHandlerMethodMatcher.matches({0},{1},{2},{3}), returning {4}", access, name, desc, annotations,matches);
		return matches;
	}

	@Override
	public Method[] getExactMethods() {
		return methodMatcher.getExactMethods();
	}

	@Override
	public int hashCode() {
		return methodMatcher.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		ResponseCompletionHandlerMethodMatcher tmp = (ResponseCompletionHandlerMethodMatcher)obj;
		
		return methodMatcher.equals(tmp.methodMatcher);
	}

	@Override
	public String toString() {
		return methodMatcher.toString();
	}

	
}
