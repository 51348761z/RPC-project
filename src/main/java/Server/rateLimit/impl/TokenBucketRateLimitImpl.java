package Server.rateLimit.impl;

import Server.rateLimit.RateLimit;

public class TokenBucketRateLimitImpl implements RateLimit {
    private static int RATE_LIMIT; // ms
    private static int CAPACITY;
    private volatile int currentCapacity;
    private volatile long timestamp = System.currentTimeMillis(); // last time a token was consumed
    public TokenBucketRateLimitImpl(int rateLimit, int capacity) {
        RATE_LIMIT = rateLimit;
        CAPACITY = capacity;
        currentCapacity = capacity;
    }
    @Override
    public synchronized boolean getToken() {
        // If there are tokens available, consume one
        if (currentCapacity > 0) {
            currentCapacity--;
            return true;
        }
        // If no tokens are available, check if we can refill the bucket
        long current = System.currentTimeMillis();
        long timePassed = current-timestamp;
        if (timePassed >= RATE_LIMIT) {
            int tokensToAdd = (int) (timePassed / RATE_LIMIT);
            currentCapacity = Math.min(currentCapacity + tokensToAdd, CAPACITY);
            timestamp += (long) tokensToAdd * RATE_LIMIT;
            return true;
        }
        return false;
    }
}
