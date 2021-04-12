package org.mule.runtime.http.api.server;

import java.util.Collection;

import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class HttpServer {

	
	public RequestHandlerManager addRequestHandler(Collection<String> methods, String path,RequestHandler requestHandler) {
		return Weaver.callOriginal();
	}

	public RequestHandlerManager addRequestHandler(String path, RequestHandler requestHandler) {
		return Weaver.callOriginal();
	}

}
