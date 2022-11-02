package com.newrelic.instrumentation.mule.http.connector;

import java.util.concurrent.ConcurrentHashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;

public class TracerUtils {

	
	private static ConcurrentHashMap<Integer, Token> tokenCache = new ConcurrentHashMap<>();
	
	public static boolean addToken(Integer hash) {
		if(!tokenCache.containsKey(hash)) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			if(token != null && token.isActive()) {
				tokenCache.put(hash, token);
				return true;
			} else {
				token.expire();
				token = null;
			}
		}
		return false;
	}
	
	public static Token getToken(Integer hash) {
		return tokenCache.remove(hash);
	}
}
