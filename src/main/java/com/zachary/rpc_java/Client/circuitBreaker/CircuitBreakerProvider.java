package com.zachary.rpc_java.Client.circuitBreaker;

import java.util.HashMap;
import java.util.Map;

/**
 * 维护不同服务的熔断器
 */
public class CircuitBreakerProvider {
    private Map<String, CircuitBreaker> circuitBreakerMap = new HashMap<>();

    public CircuitBreaker getCircuitBreaker(String serviceName) {
        CircuitBreaker circuitBreaker;
        if (circuitBreakerMap.containsKey(serviceName)) {
            circuitBreaker = circuitBreakerMap.get(serviceName);
        } else {
            circuitBreaker = new CircuitBreaker(3, 0.5, 10000);
        }
        return circuitBreaker;
    }
}
