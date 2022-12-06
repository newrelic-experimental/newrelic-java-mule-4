package com.newrelic.mule.core4_3.tracers;

import com.newrelic.agent.Transaction;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.api.agent.NewRelic;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TracerUtils {
	
	private static long THRESHOLD_IN_MS = 100L;

	public static void processTracer(Object caller, long startInNanos, long endInNanos, ClassMethodSignature sig, Map<String, Object> attributes, String... metricNameParts) {
		if (endInNanos < startInNanos)
			return; 
		boolean overThreshold = (TimeUnit.MILLISECONDS.convert(endInNanos - startInNanos, TimeUnit.NANOSECONDS) >= THRESHOLD_IN_MS);
		if (overThreshold) {
			Transaction transaction = Transaction.getTransaction(false);
			if (transaction != null) {
				MuleThresholdTracer tracer = new MuleThresholdTracer(transaction, sig, caller, startInNanos, endInNanos, String.join("/", (CharSequence[])metricNameParts));
				if (attributes != null && !attributes.isEmpty())
					tracer.addCustomAttributes(attributes); 
				tracer.finish(176, (Object)null);
				NewRelic.incrementCounter("MuleTracer/ProcessedTracer/MuleCore");
			} else {
				NewRelic.incrementCounter("MuleTracer/NoTransaction/MuleCore");
			} 
		} 
	}

	public static String getMethodDesc(Class<?> clazz, String methodName, String[] args) {
		List<String> paramNames = Arrays.asList(args);
		Method[] methods = clazz.getDeclaredMethods();
		Method matching = null;
		for (int j = 0; j < methods.length && matching == null; j++) {
			Method method = methods[j];
			if (method.getName().equals(methodName)) {
				Class<?>[] paramTypes = method.getParameterTypes();
				boolean match = true;
				for (int i = 0; i < paramTypes.length && match; i++) {
					Class<?> paramClazz = paramTypes[i];
					String classname = paramClazz.getName();
					if (!paramNames.contains(classname))
						match = false; 
				} 
				if (match)
					matching = method; 
			} 
		} 
		if (matching != null)
			Type.getMethodDescriptor(matching); 
		return "()V";
	}

	public static String getMethodDesc(Class<?> clazz, String methodName) {
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName))
				return Type.getMethodDescriptor(method); 
		} 
		return "()V";
	}
}
