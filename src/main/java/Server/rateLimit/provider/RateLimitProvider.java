package Server.rateLimit.provider;

import Server.rateLimit.RateLimit;
import Server.rateLimit.impl.TokenBucketRateLimitImpl;

import java.util.HashMap;
import java.util.Map;

public class RateLimitProvider {
    Map<String, RateLimit> rateLimitMap = new HashMap<>();

    public RateLimit getRateLimit(String name) {
        if (!rateLimitMap.containsKey(name)) {
            RateLimit rateLimit = new TokenBucketRateLimitImpl(100, 10);
            rateLimitMap.put(name, rateLimit);
            return rateLimit;
        }
        return rateLimitMap.get(name);
    }
}
