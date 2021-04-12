package org.mule.runtime.core.privileged.processor.chain;

import java.util.List;
import java.util.Optional;

import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategy;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.BaseClass)
class AbstractMessageProcessorChain {
	
	@NewField
	protected String chainName = "Unknown";
	
	AbstractMessageProcessorChain(final String name, final Optional<ProcessingStrategy> processingStrategyOptional,final List<Processor> processors) {
		if(name != null && !name.isEmpty()) {
			chainName = name;
		}
	}	

	@Trace(dispatcher=true)
	public CoreEvent process(final CoreEvent event) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","MuleProcessorChain",getClass().getSimpleName(),"process",chainName});
		CoreEvent retValue = Weaver.callOriginal();
		return retValue;
	}
	
}
