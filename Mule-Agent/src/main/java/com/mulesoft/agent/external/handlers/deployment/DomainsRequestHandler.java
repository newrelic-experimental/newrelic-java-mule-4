package com.mulesoft.agent.external.handlers.deployment;

import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weaver;

import javax.ws.rs.core.Response;

@Weave
public abstract class DomainsRequestHandler {

	
	/*
	 * Used to get rid of frequent uneventful transaction
	 */
	@Trace
	public Response listDomains() {
		NewRelic.getAgent().getTransaction().ignore();
		
		return Weaver.callOriginal();
	}
}
