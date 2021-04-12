package org.mule.runtime.core.api.construct;

import org.mule.runtime.core.api.processor.strategy.ProcessingStrategyFactory;

import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class Pipeline {

	public ProcessingStrategyFactory getProcessingStrategyFactory() {
		return Weaver.callOriginal();
	}
}
