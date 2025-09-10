package Client.CircuitBreaker;


public interface CircuitBreakerProvider {
    CircuitBreaker getCircuitBreaker(String serviceName);
}
