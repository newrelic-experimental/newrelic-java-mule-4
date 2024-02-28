package org.mule.extensions.java.internal.function;

import java.util.Map;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class JavaModuleFunctions {
	
	@Trace
	public Object invoke(String clazz, String methodName, Object instance, Map<String, Object> args) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","JavaModuleFunctions","invoke",clazz,methodName);
		return Weaver.callOriginal();
	}
}
