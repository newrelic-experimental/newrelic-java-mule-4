package org.mule.service.http.impl.service.server.grizzly;

import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mule.http.NRRequestHandler;
import org.mule.runtime.http.api.server.RequestHandler;
import org.mule.runtime.http.api.server.RequestHandlerManager;

import java.util.Collection;

@Weave(originalName = "org.mule.service.http.impl.service.server.grizzly.GrizzlyHttpServer")
public class GrizzlyHttpServer_Instrumentation {

    public RequestHandlerManager addRequestHandler(Collection<String> methods, String path, RequestHandler requestHandler) {
        if(!(requestHandler instanceof NRRequestHandler)) {
            requestHandler = new NRRequestHandler(requestHandler,path);
        }
        return  Weaver.callOriginal();
    }

    public RequestHandlerManager addRequestHandler(String path, RequestHandler requestHandler) {
        if(!(requestHandler instanceof NRRequestHandler)) {
            requestHandler = new NRRequestHandler(requestHandler,path);
        }
        return  Weaver.callOriginal();
    }
}
