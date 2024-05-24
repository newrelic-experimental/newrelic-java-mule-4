package com.newrelic.instrumentation.mule.http.connector;

import java.util.ArrayList;
import java.util.List;

import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OrClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.NameMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;

public class RequestHandlerMatcher implements ClassAndMethodMatcher {
	
	private static final String REQUESTHANDLER_INTERFACE = "org.mule.runtime.http.api.server.RequestHandler";
	private static final String MODULEREQUESTHANDLER_INTERFACE = "org.mule.extension.http.internal.listener.server.ModuleRequestHandler";
	protected static final String HANDLEREQUEST = "handleRequest";
	protected static final String CREATERESULT = "createResult";

	@Override
	public ClassMatcher getClassMatcher() {
		InterfaceMatcher reqHandlerMatcher = new InterfaceMatcher(REQUESTHANDLER_INTERFACE);
		InterfaceMatcher modReqHandlerMatcher = new InterfaceMatcher(MODULEREQUESTHANDLER_INTERFACE);
		List<ClassMatcher> matchers = new ArrayList<ClassMatcher>();
		matchers.add(modReqHandlerMatcher);
		matchers.add(reqHandlerMatcher);
		
		
		return new OrClassMatcher(matchers);
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		NameMethodMatcher matcher1 = new NameMethodMatcher(HANDLEREQUEST);
		NameMethodMatcher matcher2 = new NameMethodMatcher(CREATERESULT);
		
		return OrMethodMatcher.getMethodMatcher(matcher1,matcher2);
	}

}
