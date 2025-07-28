package Client.circuitBreaker;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public class CircuitBreakerProvider {

    @Data
    public static class Config {
        private int failureThreshold = 1;
        private double successRate = 0.5;
        private long resetPeriodMS = 10000;
    }

    private Map<String, CircuitBreaker> circuitBreakerMap = new HashMap<>();

    public CircuitBreaker getCircuitBreaker(String serviceName) {
        return getCircuitBreaker(serviceName, new Config());
    }
    public synchronized CircuitBreaker getCircuitBreaker(String serviceName, Config config) {
        CircuitBreaker circuitBreaker;

        if (circuitBreakerMap.containsKey(serviceName)) {
            circuitBreaker = circuitBreakerMap.get(serviceName);
        } else {
            System.out.println("Creating new CircuitBreaker for service: " + serviceName);
            circuitBreaker = new CircuitBreaker(config.failureThreshold, config.successRate, config.resetPeriodMS);
            circuitBreakerMap.put(serviceName, circuitBreaker);
        }

        return circuitBreaker;
    }
}
