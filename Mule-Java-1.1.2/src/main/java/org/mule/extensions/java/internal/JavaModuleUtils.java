package org.mule.extensions.java.internal;

import java.lang.reflect.Method;
import java.util.Map;

import org.mule.extensions.java.internal.parameters.ExecutableIdentifier;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.core.api.el.ExpressionManager;
import org.slf4j.Logger;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class JavaModuleUtils {

	@Trace
	public static Object invokeMethod(Method method, Map<String, TypedValue<Object>> args,
            Object instance, ExecutableIdentifier identifier,
            TransformationService transformationService, ExpressionManager expressionManager,
            Logger logger) {
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
