package com.newrelic.instrumentation.mule.http.connector;

import java.util.logging.Level;

import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OrClassMatcher;
import com.newrelic.api.agent.NewRelic;

public class HttpResponseReadyCallbackClassMatcher extends OrClassMatcher {

	public HttpResponseReadyCallbackClassMatcher() {
		super(new InterfaceMatcher("org.mule.runtime.http.api.server.async.HttpResponseReadyCallback"), new ExactClassMatcher("org.mule.service.http.impl.service.server.grizzly.GrizzlyRequestDispatcherFilter$1"));
	}

	@Override
	public boolean isMatch(ClassLoader loader, ClassReader cr) {
		
		boolean b = super.isMatch(loader, cr);
		NewRelic.getAgent().getLogger().log(Level.FINE, "HttpResponseReadyCallbackClassMatcher.isMatch for {0} is {1}", cr.getClassName(), b);
		return b;
	}

	@Override
	public boolean isMatch(Class<?> clazz) {
		
		boolean b = super.isMatch(clazz);
		NewRelic.getAgent().getLogger().log(Level.FINE, "HttpResponseReadyCallbackClassMatcher.isMatch for {0} is {1}", clazz.getName(), b);
		return b;
	}
	
	
}
