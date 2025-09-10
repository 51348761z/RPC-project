package Client.CircuitBreaker;

import Client.CircuitBreaker.CircuitBreaker;
import java.util.concurrent.atomic.AtomicInteger;

public class CircuitBreakerImpl implements CircuitBreaker {
    enum CircuitBreakerState {
        CLOSED, OPEN, HALF_OPEN
    }
    private CircuitBreakerState state = CircuitBreakerState.CLOSED; // Initial state is CLOSED
    private AtomicInteger failureCount = new AtomicInteger(0); // Count of failures
    private AtomicInteger successCount = new AtomicInteger(0); // Count of successes
    private AtomicInteger requestCount = new AtomicInteger(0); // Count of requests
    private final int failureThreshold; // Threshold for failures to open the circuit
    private final double halfOpenSuccessRate; // Success rate required to close the circuit in HALF_OPEN state
    private final long resetTimePeriodMS; // Time period to reset the circuit
    private long lastFailureTime = 0; // Last time the circuit was opened

    public CircuitBreakerImpl(int failureThreshold, double halfOpenSuccessRate, long resetTimePeriodMS) {
        this.failureThreshold = failureThreshold;
        this.halfOpenSuccessRate = halfOpenSuccessRate;
        this.resetTimePeriodMS = resetTimePeriodMS;
    }

    public synchronized boolean allowRequest() {
        long currentTime = System.currentTimeMillis();
        return switch (state) {
            case OPEN -> {
                if (currentTime - lastFailureTime > resetTimePeriodMS) {
                    state = CircuitBreakerState.HALF_OPEN;
                    resetCounts();
                    yield true;
                } else {
                    yield false;
                }
            }
            case HALF_OPEN -> {
                requestCount.incrementAndGet();
                yield true;
            }
            case CLOSED -> true;
        };
    }

    public synchronized void recordSuccess() {
        successCount.incrementAndGet();

        if (state == CircuitBreakerState.HALF_OPEN) {
            double currentSuccessRate = (double) successCount.get() / requestCount.get();
            if (currentSuccessRate > halfOpenSuccessRate) {
                state = CircuitBreakerState.CLOSED;
                resetCounts();
            } else if (state == CircuitBreakerState.CLOSED) {
                failureCount.set(0); // Reset failure count if circuit is closed
            }
        }
    }

    public synchronized void recordFailure() {
        failureCount.incrementAndGet();
        successCount.set(0);
        lastFailureTime = System.currentTimeMillis();

        if (state == CircuitBreakerState.HALF_OPEN) {
            // If in HALF_OPEN state and failure occurs, open the circuit
            state = CircuitBreakerState.OPEN;
            resetCounts();
        } else if (state == CircuitBreakerState.CLOSED && failureCount.get() >= failureThreshold) {
            // Open the circuit if failure threshold is reached in CLOSED state
            state = CircuitBreakerState.OPEN;
        }
    }

    private void resetCounts() {
        failureCount.set(0);
        successCount.set(0);
        requestCount.set(0);
    }
}
