package org.mule.runtime.core.internal.event;

import java.util.Optional;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.event.EventContext;
import org.mule.runtime.core.api.exception.FlowExceptionHandler;
import org.mule.runtime.core.privileged.event.BaseEventContext;

import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class DefaultEventContext extends AbstractEventContext {
	
  public static BaseEventContext child(BaseEventContext parent, Optional<ComponentLocation> componentLocation, FlowExceptionHandler exceptionHandler) {
	  
    BaseEventContext ctx = (BaseEventContext)Weaver.callOriginal();
    AbstractEventContext actx = (AbstractEventContext)parent;
    if (actx.headers != null) {
      ((AbstractEventContext)ctx).headers = actx.headers;
    } else {
      MuleUtils.setHeaders((EventContext)parent);
      MuleUtils.setHeaders((EventContext)ctx);
    } 
    return ctx;
  }
}
