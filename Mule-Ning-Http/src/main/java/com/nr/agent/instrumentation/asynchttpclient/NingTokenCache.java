package com.nr.agent.instrumentation.asynchttpclient;

import java.util.concurrent.ConcurrentHashMap;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;

/**
 * Token cache for ning async HTTP handler wrapper chain.
 *
 * Flow:
 * 1. execute() stores {token, segment} keyed by outer handler identity
 * 2. onStatusReceived() on inner handler calls transferToHandler(this)
 *    which finds the existing entry and re-keys it to the inner handler
 * 3. onCompleted() on inner handler calls getAndClear(this) to retrieve
 */
public class NingTokenCache {

    private static ConcurrentHashMap<Integer, TokenAndSegment> cache = new ConcurrentHashMap<>();

    public static void store(Object handler, Token token, Segment segment, java.net.URI uri) {
        if (handler != null && token != null) {
            cache.put(System.identityHashCode(handler), new TokenAndSegment(token, segment, uri));
        }
    }

    // Called from onStatusReceived on inner handler — find any existing entry and re-key to this handler
    public static void transferToHandler(Object innerHandler) {
        if (innerHandler == null || cache.isEmpty()) return;
        int innerKey = System.identityHashCode(innerHandler);
        // Already have an entry for this handler
        if (cache.containsKey(innerKey)) return;

        // Find first entry with a valid token (from execute on outer handler) and re-key it
        for (java.util.Map.Entry<Integer, TokenAndSegment> entry : cache.entrySet()) {
            if (entry.getValue().token != null && entry.getValue().token.isActive()) {
                TokenAndSegment ts = cache.remove(entry.getKey());
                if (ts != null) {
                    cache.put(innerKey, ts);
                    return;
                }
            }
        }
    }

    public static TokenAndSegment getAndClear(Object handler) {
        if (handler != null) {
            return cache.remove(System.identityHashCode(handler));
        }
        return null;
    }

    public static int cacheSize() {
        return cache.size();
    }

    public static class TokenAndSegment {
        public Token token;
        public Segment segment;
        public java.net.URI uri;
        public TokenAndSegment(Token token, Segment segment, java.net.URI uri) {
            this.token = token;
            this.segment = segment;
            this.uri = uri;
        }
    }
}
