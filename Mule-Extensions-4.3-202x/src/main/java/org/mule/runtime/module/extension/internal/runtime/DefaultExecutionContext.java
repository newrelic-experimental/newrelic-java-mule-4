package org.mule.runtime.module.extension.internal.runtime;

import org.mule.runtime.api.meta.model.ComponentModel;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.module.extension.api.runtime.privileged.ExecutionContextAdapter;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;


@Weave
public abstract class DefaultExecutionContext<M extends ComponentModel> implements ExecutionContextAdapter<M>  {

	
	@Trace(dispatcher=true)
	public void changeEvent(CoreEvent updated) {
		Weaver.callOriginal();
	}
}
