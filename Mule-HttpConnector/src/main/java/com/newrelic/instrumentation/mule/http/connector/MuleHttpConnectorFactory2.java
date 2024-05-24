package com.newrelic.instrumentation.mule.http.connector;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.AbstractTracerFactory;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.TracerFlags;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;

public class MuleHttpConnectorFactory2 extends AbstractTracerFactory {


	@Override
	public Tracer doGetTracer(Transaction transaction, ClassMethodSignature sig, Object object, Object[] args) {

		String classname = object.getClass().getName();
		MetricNameFormat format = null;
		String methodName = sig.getMethodName();
		Token token = null;
		NewRelic.getAgent().getLogger().log(Level.FINE, "MuleHttpConnectorFactory2.getTracer({0},{1},{2},{3})", transaction,sig,object,args);
		
		if(classname.equals(HttpListenerClassMethodMatcher.HTTPLISTENER)) {
			format = new SimpleMetricNameFormat("Custom/HttpListener/"+methodName);
		} else if(classname.equals(HttpRequesterClassMethodMatcher.HTTPREQUESTER)) {
			format = new SimpleMetricNameFormat("Custom/HttpRequester/onRequest");
		} else if(classname.equals(ResponseSenderMatcher.RESPONSESENDER)) {
			format = new SimpleMetricNameFormat("Custom/HttpListenerResponseSender/sendResponse");
		} else {
			if(methodName.equals(RequestHandlerMatcher.CREATERESULT)) {
				format = new SimpleMetricNameFormat("Custom/ModuleRequestHandler/createResult");
				if(args.length > 1) {

				}
			} else if(methodName.equals(RequestHandlerMatcher.HANDLEREQUEST)) {
				format = new SimpleMetricNameFormat("Custom/RequestHandler/handleRequest");
				if(args.length > 1) {
					Integer hash = args[1].hashCode();
					boolean b = TracerUtils.addToken(hash);
					if(b) {
						NewRelic.getAgent().getLogger().log(Level.FINE, "MuleHttpConnectorFactory2 created token for object with hash {0}", hash);
					}
				}
			} else if(methodName.endsWith(HttpResponseReadyCallbackClassMethodMatcher.RESPONSE_READY)) {
				NewRelic.getAgent().getLogger().log(Level.FINE, "MuleHttpConnectorFactory2 creating tracer for HttpResponseReadyCallback.responseReady, object: {0}", object);
				
				format = new SimpleMetricNameFormat("Custom/HttpResponseReadyCallback/responseReady");
				Integer hash = object.hashCode();
				token = TracerUtils.getToken(hash);
				NewRelic.getAgent().getLogger().log(Level.FINE, "MuleHttpConnectorFactory2 retrieved token {0} and object with hash {1} and method responseReady", token, hash);

			} else if(methodName.equals(HttpResponseReadyCallbackClassMethodMatcher.START_RESPONSE)) {
				NewRelic.getAgent().getLogger().log(Level.FINE, "MuleHttpConnectorFactory2 creating tracer for HttpResponseReadyCallback.startResponse, object: {0}", object);
				format = new SimpleMetricNameFormat("Custom/HttpResponseReadyCallback/startResponse");
				Integer hash = object.hashCode();
				token = TracerUtils.getToken(hash);
				NewRelic.getAgent().getLogger().log(Level.FINE, "MuleHttpConnectorFactory2 retrieved token {0} and object with hash {1} and method startResponse", token, hash);

			}
		}
		if(format != null) {
			if(token != null) {
				HttpListenerTracer t = new HttpListenerTracer(transaction, sig, object,format,TracerFlags.DISPATCHER|TracerFlags.ASYNC|DefaultTracer.DEFAULT_TRACER_FLAGS);
				t.setAsync(true);
				t.setToken(token);
				return t;
			}
			return new HttpListenerTracer(transaction, sig, object,format);
		}
		return new HttpListenerTracer(transaction, sig, object);
	}

}
