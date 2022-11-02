package com.newrelic.instrumentation.mule.http.connector;

import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.AbstractTracerFactory;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;

public class MuleHttpConnectorFactory extends AbstractTracerFactory {
	
	

	@Override
	public Tracer doGetTracer(Transaction transaction, ClassMethodSignature sig, Object object, Object[] args) {
		
		String classname = object.getClass().getName();
		MetricNameFormat format = null;
		String methodName = sig.getMethodName();
		if(classname.equals(HttpListenerClassMethodMatcher.HTTPLISTENER)) {
			format = new SimpleMetricNameFormat("Custom/HttpListener/"+methodName);
		} else if(classname.equals(HttpRequesterClassMethodMatcher.HTTPREQUESTER)) {
			format = new SimpleMetricNameFormat("Custom/HttpRequester/onRequest");
		} else if(classname.equals(ResponseSenderMatcher.RESPONSESENDER)) {
			format = new SimpleMetricNameFormat("Custom/HttpListenerResponseSender/sendResponse");
		} else {
			if(methodName.equals(RequestHandlerMatcher.CREATERESULT)) {
				format = new SimpleMetricNameFormat("Custom/ModuleRequestHandler/createResult");
			} else if(methodName.equals(RequestHandlerMatcher.HANDLEREQUEST)) {
				format = new SimpleMetricNameFormat("Custom/RequestHandler/handleRequest");
			}
		}
		if(format != null) {
			return new HttpListenerTracer(transaction, sig, object,format);
		}
		return new HttpListenerTracer(transaction, sig, object);
	}

}
