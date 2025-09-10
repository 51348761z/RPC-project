package Client.CircuitBreaker;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import Client.CircuitBreaker.CircuitBreaker;
import Client.CircuitBreaker.CircuitBreakerProvider;

public class CircuitBreakerProviderImpl implements CircuitBreakerProvider {

    @Data
    public static class Config {
        private int failureThreshold = 1;
        private double successRate = 0.5;
        private long resetPeriodMS = 10000;
    }

    private Map<String, CircuitBreakerImpl> circuitBreakerMap = new HashMap<>();

    public CircuitBreaker getCircuitBreaker(String serviceName) {
        return getCircuitBreaker(serviceName, new Config());
    }
    public synchronized CircuitBreakerImpl getCircuitBreaker(String serviceName, Config config) {
        CircuitBreakerImpl circuitBreaker;

        if (circuitBreakerMap.containsKey(serviceName)) {
            circuitBreaker = circuitBreakerMap.get(serviceName);
        } else {
            System.out.println("Creating new CircuitBreaker for service: " + serviceName);
            circuitBreaker = new CircuitBreakerImpl(config.failureThreshold, config.successRate, config.resetPeriodMS);
            circuitBreakerMap.put(serviceName, circuitBreaker);
        }

        return circuitBreaker;
    }
}
