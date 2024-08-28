package org.mule.runtime.core.internal.execution;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class ClassLoaderInjectorInvocationHandler {
	
	private final Object delegate = Weaver.callOriginal();
	
	@Trace
	public Object invoke(Object proxy, Method method, Object[] args) {
		List<String> names = new ArrayList<String>();
		names.add("Custom");
		names.add("ClassLoaderInjectorInvocationHandler");
		if(proxy != null) {
			String tmp = proxy.getClass().getSimpleName();
			if(!tmp.startsWith("$")) {
				names.add(proxy.getClass().getSimpleName());
			}
		}
		if(delegate != null) {
			String tmp = delegate.getClass().getSimpleName();
			if(!tmp.startsWith("$")) {
				names.add(delegate.getClass().getSimpleName());
			}
		}
		if(method != null) {
			Class<?> declaring = method.getDeclaringClass();
			if(declaring != null) {
				names.add(declaring.getSimpleName());
			}
			String methodName = method.getName();
			names.add(methodName);
		}
		String[] namesArray = new String[names.size()];
		names.toArray(namesArray);
		NewRelic.getAgent().getTracedMethod().setMetricName(namesArray);
		return Weaver.callOriginal();
	}
}
