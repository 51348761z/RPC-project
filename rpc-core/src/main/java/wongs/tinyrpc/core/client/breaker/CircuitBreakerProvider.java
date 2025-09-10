package wongs.tinyrpc.core.client.breaker;


public interface CircuitBreakerProvider {
    CircuitBreaker getCircuitBreaker(String serviceName);
}
