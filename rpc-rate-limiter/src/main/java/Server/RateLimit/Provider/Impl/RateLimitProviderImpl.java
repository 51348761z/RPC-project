package Server.RateLimit.Provider.Impl;

import Server.RateLimit.RateLimit;
import Server.RateLimit.RateLimitProvider;
import Server.RateLimit.Impl.TokenBucketRateLimitImpl;

import java.util.HashMap;
import java.util.Map;

public class RateLimitProviderImpl implements RateLimitProvider {
    Map<String, RateLimit> rateLimitMap = new HashMap<>();

    @Override
    public RateLimit getRateLimit(String name) {
        if (!rateLimitMap.containsKey(name)) {
            RateLimit rateLimit = new TokenBucketRateLimitImpl(100, 10);
            rateLimitMap.put(name, rateLimit);
            return rateLimit;
        }
        return rateLimitMap.get(name);
    }
}
