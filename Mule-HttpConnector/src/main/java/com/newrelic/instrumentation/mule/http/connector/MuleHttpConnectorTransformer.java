package com.newrelic.instrumentation.mule.http.connector;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.newrelic.agent.InstrumentationProxy;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher.Match;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcherBuilder;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import com.newrelic.agent.instrumentation.context.ContextClassTransformer;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.agent.instrumentation.context.InstrumentationContextManager;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.tracing.TraceDetailsBuilder;
import com.newrelic.api.agent.NewRelic;

public class MuleHttpConnectorTransformer implements ContextClassTransformer {
	
	private final InstrumentationContextManager contextManager;
	private final Map<String, ClassMatchVisitorFactory> matchers = new HashMap<String, ClassMatchVisitorFactory>();
	private final Map<String, String> factoryMappings = new HashMap<>();
	
	public MuleHttpConnectorTransformer(InstrumentationContextManager mgr,InstrumentationProxy pInstrumentation) {
		contextManager = mgr;
	}

    protected void addMatcher(ClassAndMethodMatcher matcher,String tracerFactory) {
    	NewRelic.getAgent().getLogger().log(Level.FINE, "Call to MuleHttpConnectorTransformer.addMatcher({0})", matcher);
    	OptimizedClassMatcherBuilder builder = OptimizedClassMatcherBuilder.newBuilder();
        builder.addClassMethodMatcher(matcher);
        ClassMatchVisitorFactory matchVisitor = builder.build();
        String simpleName = matcher.getClass().getSimpleName();
        matchers.put(simpleName, matchVisitor);
        
        factoryMappings.put(simpleName, tracerFactory);
    	contextManager.addContextClassTransformer(matchVisitor, this);
    }
    
    protected void removeMatcher(ClassAndMethodMatcher matcher) {
    	ClassMatchVisitorFactory matchVisitor = matchers.get(matcher.getClass().getSimpleName());
    	if(matchVisitor != null) {
    		contextManager.removeMatchVisitor(matchVisitor);
    	}
    }

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer, InstrumentationContext context, Match match)
			throws IllegalClassFormatException {

    	NewRelic.getAgent().getLogger().log(Level.FINE, "Call to MuleHttpConnectorTransformer.transform({0},{1},{2},{3},{4},{5},{6})", loader,className,classBeingRedefined,protectionDomain,classfileBuffer,context,match);

		for (Method method : match.getMethods()) {
			for (ClassAndMethodMatcher matcher : match.getClassMatches().keySet()) {
                if (matcher.getMethodMatcher().matches(MethodMatcher.UNSPECIFIED_ACCESS, method.getName(),
                        method.getDescriptor(), match.getMethodAnnotations(method))) {
                	String factoryName = factoryMappings.getOrDefault(matcher.getClass().getSimpleName(), MuleHttpConnectorService.TRACER_FACTORY_NAME);
                	
                    context.putTraceAnnotation(method, TraceDetailsBuilder.newBuilder().setTracerFactoryName(factoryName).build());
                	NewRelic.getAgent().getLogger().log(Level.FINE, "Call to MuleHttpConnectorTransformer.transform matched method {0} to matcher {1} and uses tracerfactory {2}", method.getName(), matcher,factoryName);
                  
                }
				
			}
		}
		
		return null;
	}

	
}
