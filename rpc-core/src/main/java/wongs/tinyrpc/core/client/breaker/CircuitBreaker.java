package wongs.tinyrpc.core.client.breaker;

public interface CircuitBreaker {
    boolean allowRequest();
    void recordSuccess();
    void recordFailure();
}
