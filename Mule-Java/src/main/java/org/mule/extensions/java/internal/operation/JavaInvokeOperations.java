package org.mule.extensions.java.internal.operation;

import java.util.Map;

import org.mule.extensions.java.internal.parameters.MethodIdentifier;
import org.mule.extensions.java.internal.parameters.StaticMethodIdentifier;
import org.mule.runtime.api.metadata.TypedValue;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class JavaInvokeOperations {

	@Trace
	public Object invoke(MethodIdentifier identifier, Object instance, Map<String, TypedValue<Object>> args) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","JavaInvokeOperations","invoke",identifier.getClazz(),identifier.getElementId());
		return Weaver.callOriginal();
	}
	
	public Object invokeStatic(StaticMethodIdentifier identifier, Map<String, TypedValue<Object>> args) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","JavaInvokeOperations","invokeStatic",identifier.getClazz(),identifier.getElementId());
		return Weaver.callOriginal();
	}
}
