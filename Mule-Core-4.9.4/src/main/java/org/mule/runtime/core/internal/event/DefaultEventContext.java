package org.mule.runtime.core.internal.event;

import java.util.Optional;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.event.EventContext;
import org.mule.runtime.core.api.exception.FlowExceptionHandler;
import org.mule.runtime.core.privileged.event.BaseEventContext;

import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.WeaveAllConstructors;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class DefaultEventContext extends AbstractEventContext {

	// @WeaveAllConstructors causes "Could not find required field name: headers"
	// because the weaver can't resolve inherited @NewField from parent during package validation
	// Headers will be initialized lazily in child() and addChildContext()
	// @WeaveAllConstructors
	// public DefaultEventContext() {
	// 	if(headers == null) {
	// 		headers = MuleUtils.getHeaders(getRootContext());
	// 		if(headers == null || headers.isEmpty()) {
	// 			try {
	// 				BaseEventContext root = getRootContext();
	// 				if (root != null) {
	// 					MuleUtils.setHeaders(root);
	// 				}
	// 			} catch (NullPointerException e) {
	// 			}
	// 		}
	// 	}
	// }

  // PHASE 2: Re-enabled (original method)
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
