package org.mule.module.apikit;

import java.lang.reflect.Method;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.WeaveAllConstructors;

@Weave
public abstract class Router {

	@WeaveAllConstructors
	public Router() {
		Class<?> thisClass = getClass();
		Method[] methods = thisClass.getDeclaredMethods();
		
		for(Method method : methods) {
			if(method.getName().equalsIgnoreCase("doroute")) {
				AgentBridge.instrumentation.instrument(method, "MuleAPIKIT/Route");
			}
		}
	}

}
