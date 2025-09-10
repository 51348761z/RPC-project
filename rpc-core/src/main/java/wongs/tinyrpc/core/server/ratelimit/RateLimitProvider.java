package wongs.tinyrpc.core.server.ratelimit;

public interface RateLimitProvider {
    RateLimit getRateLimit(String serviceName);
}
