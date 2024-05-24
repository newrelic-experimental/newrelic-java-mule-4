package com.newrelic.instrumentation.mule.http.connector;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.newrelic.agent.InstrumentationProxy;
import com.newrelic.agent.TracerService;
import com.newrelic.agent.core.CoreService;
import com.newrelic.agent.instrumentation.ClassTransformerService;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.context.InstrumentationContextManager;
import com.newrelic.agent.service.AbstractService;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.api.agent.NewRelic;

public class MuleHttpConnectorService extends AbstractService {
	
	
	public static final String TRACER_FACTORY_NAME = "MuleHttpConnector";
	public static final String TRACER_FACTORY_NAME2 = "MuleHttpConnector2";
	
	public MuleHttpConnectorService() {
		super("MuleHttpConnectorService");
	}
 
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	protected void doStart() throws Exception {
		boolean started = setup();
		if(!started) {
			Executors.newSingleThreadScheduledExecutor().schedule(new SetupThread(), 10L, TimeUnit.SECONDS);
		}
	}

	@Override
	protected void doStop() throws Exception {

	}

	public boolean setup() {
		TracerService tracerService = ServiceFactory.getTracerService();
		ClassTransformerService classTransformerService = ServiceFactory.getClassTransformerService();
		CoreService coreService = ServiceFactory.getCoreService();
		if(classTransformerService != null && coreService != null && tracerService != null) {
			
			tracerService.registerTracerFactory(TRACER_FACTORY_NAME, new MuleHttpConnectorFactory());
			tracerService.registerTracerFactory(TRACER_FACTORY_NAME2, new MuleHttpConnectorFactory2());
			
			InstrumentationContextManager contextMgr = classTransformerService.getContextManager();
			InstrumentationProxy proxy = coreService.getInstrumentation();
			if(contextMgr != null && proxy != null) {
				MuleHttpConnectorTransformer transformer = new MuleHttpConnectorTransformer(contextMgr, proxy);
				ClassAndMethodMatcher matcher = new HttpListenerClassMethodMatcher();
				transformer.addMatcher(matcher,TRACER_FACTORY_NAME);
				transformer.addMatcher(new HttpRequesterClassMethodMatcher(),TRACER_FACTORY_NAME);
				transformer.addMatcher(new RequestHandlerMatcher(),TRACER_FACTORY_NAME);
				transformer.addMatcher(new ResponseSenderMatcher(),TRACER_FACTORY_NAME);
				transformer.addMatcher(new HttpResponseReadyCallbackClassMethodMatcher(),TRACER_FACTORY_NAME);
				NewRelic.getAgent().getLogger().log(Level.INFO, "Mule HttpListener transformer started");
				return true;
			}
		}
		
		return false;
	}
	
	private class SetupThread extends Thread {

		@Override
		public void run() {
			boolean started = false;
			
			while(!started) {
				started = setup();
				if(!started) {
					try {
						Thread.sleep(3000L);
					} catch (InterruptedException e) {
					}
				}
			}
			
		}
		
		
	}
}
