package org.mule.runtime.core.privileged.processor.chain;

//import org.mule.runtime.core.api.event.CoreEvent;
//import org.mule.runtime.core.internal.event.MuleUtils;
//import org.reactivestreams.Subscription;
//
//import com.newrelic.api.agent.NewRelic;
//import com.newrelic.api.agent.Token;
//import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
//import com.newrelic.api.agent.weaver.Weaver;
//
//import reactor.util.context.Context;

@Weave
class AbstractMessageProcessorChain$1 {
	
//	final org.mule.runtime.core.privileged.processor.chain.AbstractMessageProcessorChain this$0 = Weaver.callOriginal();
//	
//	@Trace(async=true)
//	public void onNext(final CoreEvent event) {
//		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","MessageProcessorSubscriber",this$0.chainName,"onNext"});
//		Token token = MuleUtils.getToken(event);
//		if(token != null) {
//			token.link();
//		}
//		Weaver.callOriginal();
//	}
//
//	@Trace
//	public void onError(final Throwable throwable) {
//		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","MessageProcessorSubscriber",this$0.chainName,"onError"});
//		Weaver.callOriginal();
//	}
//
//	@Trace
//	public void onComplete() {
//		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","MessageProcessorSubscriber",this$0.chainName,"onComplete"});
//		Weaver.callOriginal();
//	}
//
//	public Context currentContext() {
//		return Weaver.callOriginal();
//	}
//
//	@Trace(excludeFromTransactionTrace=true)
//	public void onSubscribe(final Subscription s) {
//		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","MessageProcessorSubscriber",this$0.chainName,"onSubscribe"});
//		Weaver.callOriginal();
//	}
	
}
