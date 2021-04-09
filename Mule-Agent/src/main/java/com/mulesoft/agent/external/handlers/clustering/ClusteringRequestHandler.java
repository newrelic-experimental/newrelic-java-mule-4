package com.mulesoft.agent.external.handlers.clustering;

import javax.ws.rs.core.Response;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class ClusteringRequestHandler {

	public Response getClusterMembers() {
		NewRelic.getAgent().getTransaction().ignore();
		return Weaver.callOriginal();
	}
	
	public Response getClusterMembership() {
		NewRelic.getAgent().getTransaction().ignore();
		return Weaver.callOriginal();
	}

		
}
