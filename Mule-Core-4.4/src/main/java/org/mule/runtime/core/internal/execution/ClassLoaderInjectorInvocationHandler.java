package org.mule.runtime.core.internal.execution;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.mule.core4_4.tracers.TracerUtils;

@Weave
public abstract class ClassLoaderInjectorInvocationHandler {
	
	private final Object delegate = Weaver.callOriginal();
	
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
		long start = System.nanoTime();
		Object result = Weaver.callOriginal();
		long end = System.nanoTime();
		ClassMethodSignature sig = new ClassMethodSignature(getClass().getName(), "invoke", TracerUtils.getMethodDesc(getClass(), "invoke"));
		TracerUtils.processTracer(this, start, end, sig, null, namesArray);
		return result;
	}
}
