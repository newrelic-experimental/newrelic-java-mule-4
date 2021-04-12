package org.mule.extensions.java.internal.util;

import java.lang.reflect.Method;
import java.util.Map;

import org.mule.extensions.java.internal.parameters.ExecutableIdentifier;
import org.mule.extensions.java.internal.transformer.ParametersTransformationResult;
import org.mule.runtime.api.metadata.TypedValue;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public class MethodInvoker {

	@Trace
	private static Object doInvoke(Method method, Map<String, TypedValue<Object>> args, Object instance,
            ExecutableIdentifier identifier, ParametersTransformationResult transformationResult) {
		if(method != null) {
			String methodName = method.getName();
			String classname = method.getDeclaringClass().getName();
			if(classname != null && methodName != null) {
				NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Mule-Java","invokeMethod",classname,methodName);
			}
		}
		return Weaver.callOriginal();
	}
}
