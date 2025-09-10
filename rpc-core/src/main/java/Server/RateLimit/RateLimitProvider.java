package Server.RateLimit;

public interface RateLimitProvider {
    RateLimit getRateLimit(String serviceName);
}
