package com.nr.instrumentation.mule.http;

import java.util.logging.Level;

import com.newrelic.agent.Agent;
import com.newrelic.agent.MetricNames;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.PointCutClassTransformer;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;

public class ResponseCompletionHandlerPointcut extends TracerFactoryPointCut {
	
	
	public ResponseCompletionHandlerPointcut(PointCutClassTransformer classTransformer) {
		super(ResponseCompletionHandlerPointcut.class, new ExactClassMatcher("org.mule.service.http.impl.service.server.grizzly.BaseResponseCompletionHandler"),new ResponseCompletionHandlerMethodMatcher());
		setPriority(DEFAULT_PRIORITY);
		Agent.LOG.log(Level.FINE, "call to ResponseCompletionHandlerPointcut.<init>({0})", classTransformer);
	}

	
	@Override
	public Tracer getTracer(Transaction transaction, ClassMethodSignature sig, Object object, Object[] args) {
		Tracer tracer = doGetTracer(transaction, sig, object, args);
		Agent.LOG.log(Level.FINE, "call to ResponseCompletionHandlerPointcut.getTracer({0},{1},{2},{3}), returning {4}", transaction,sig,object,args,tracer);
		
		return tracer;
	}


	@Override
	protected Tracer doGetTracer(Transaction transaction, ClassMethodSignature sig, Object registry, Object[] args) {
		Object response = args[1];
		NRExtendedResponse wrapper = new NRExtendedResponse(response);
		transaction.setWebResponse(wrapper);
		transaction.addOutboundResponseHeaders();
		Tracer tracer = new MuleHttpTracer(transaction, sig, registry);
		Agent.LOG.log(Level.FINE, "call to ResponseCompletionHandlerPointcut.getTracer({0},{1},{2},{3}), returning {4}", transaction,sig,registry,args,tracer);
		return tracer;
	}


	@Override
	public boolean isDispatcher() {
		return true;
	}


	private final class MuleHttpTracer extends DefaultTracer {
		public MuleHttpTracer(Transaction transaction, ClassMethodSignature sig, Object registry) {
			super(transaction, sig, registry, new ClassMethodMetricNameFormat(sig, registry, MetricNames.OTHER_TRANSACTION+"/ResponseCompletionHandler"));
			Agent.LOG.log(Level.FINE, "call to MuleHttpTracer.<init>({0},{1},{2})", transaction, sig, registry);
		}

	}

}
