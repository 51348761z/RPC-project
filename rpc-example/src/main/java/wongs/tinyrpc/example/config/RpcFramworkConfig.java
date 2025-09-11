package wongs.tinyrpc.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import wongs.tinyrpc.balancer.ConsistenctyHashBalancer;
import wongs.tinyrpc.balancer.RandomLoadBalancer;
import wongs.tinyrpc.balancer.RoundLoadBalancer;
import wongs.tinyrpc.core.client.balancer.LoadBalancer;
import wongs.tinyrpc.core.client.breaker.CircuitBreakerProvider;
import wongs.tinyrpc.core.client.discovery.ServiceDiscovery;
import wongs.tinyrpc.core.client.proxy.ClientProxy;
import wongs.tinyrpc.core.client.retry.RetryStrategy;
import wongs.tinyrpc.core.client.transport.RpcClient;
import wongs.tinyrpc.core.server.provider.ServiceProvider;
import wongs.tinyrpc.core.server.ratelimit.RateLimitProvider;
import wongs.tinyrpc.core.server.registry.ServiceRegistry;
import wongs.tinyrpc.core.server.transport.RpcServer;
import wongs.tinyrpc.fault.breaker.CircuitBreakerProviderImpl;
import wongs.tinyrpc.fault.retry.GuavaRetryStrategy;
import wongs.tinyrpc.limiter.RateLimitProviderImpl;
import wongs.tinyrpc.registry.zookeeper.ZookeeperServiceDiscovery;
import wongs.tinyrpc.registry.zookeeper.ZookeeperServiceRegistry;
import wongs.tinyrpc.transport.netty.client.NettyRpcClient;
import wongs.tinyrpc.transport.netty.server.NettyRpcServer;
import wongs.tinyrpc.transport.serializer.Impl.JsonSerializer;
import wongs.tinyrpc.transport.serializer.Impl.ObjectSerializer;
import wongs.tinyrpc.transport.serializer.Serializer;
import wongs.tinyrpc.transport.socket.SimpleRPCServer;

@Configuration
@PropertySource("classpath:application.properties")
public class RpcFramworkConfig {
    // use @Value to inject properties
    @Value("${tinyrpc.server.host:127.0.0.1}") // default value
    private String serverHost;

    @Value("${tinyrpc.server.port:9999}")
    private int serverPort;

    @Value("${tinyrpc.server.type:netty}")
    private String serverType;

    @Value("${tinyrpc.client.loadbalancer.stratergy:random}")
    private String loadBalancerStratergy;

    @Value("${tinyrpc.client.serializer.type:json}")
    private String serializerType;

    // client components configuration
    @Bean
    public ServiceDiscovery serviceDiscovery() throws InterruptedException {
        return new ZookeeperServiceDiscovery();
    }

    @Bean
    public LoadBalancer loadBalancer() {
        return switch (loadBalancerStratergy.toLowerCase()) {
            case "random" -> new RandomLoadBalancer();
            case "roundrboin" -> new RoundLoadBalancer();
            case "consistenhash" -> new ConsistenctyHashBalancer();
            default -> throw new IllegalArgumentException("Unsupported load balancer strategy: " + loadBalancerStratergy);
        };
    }

    @Bean
    public RpcClient rpcClient(ServiceDiscovery serviceDiscovery, Serializer serializer) throws InterruptedException {
        return new NettyRpcClient(serviceDiscovery, serializer);
    }

    @Bean
    RetryStrategy retryStrategy() {
        return new GuavaRetryStrategy();
    }

    @Bean
    CircuitBreakerProvider circuitBreakerProvider() {
        return new CircuitBreakerProviderImpl();
    }

    @Bean
    ClientProxy clientProxy(RpcClient rpcClient, ServiceDiscovery serviceDiscovery, CircuitBreakerProvider circuitBreakerProvider, RetryStrategy retryStrategy) throws InterruptedException {
        return new ClientProxy(rpcClient, serviceDiscovery, circuitBreakerProvider, retryStrategy);
    }

    // server components configuration
    @Bean
    ServiceRegistry serviceRegistry() {
        return new ZookeeperServiceRegistry();
    }

    @Bean
    RateLimitProvider rateLimitProvider() {
        return new RateLimitProviderImpl();
    }

    @Bean
    ServiceProvider serviceProvider(ServiceRegistry serviceRegistry, RateLimitProvider rateLimitProvider) {
        return new ServiceProvider(serverHost, serverPort, serviceRegistry, rateLimitProvider);
    }

    @Bean
    RpcServer rpcServer(ServiceProvider serviceProvider) {
        return switch (serverType.toLowerCase()) {
            case "netty" -> new NettyRpcServer(serviceProvider);
            case "socket" -> new SimpleRPCServer(serviceProvider);
            default -> throw new IllegalArgumentException("Unsupported server type: " + serverType);
        };
    }

    // public components configuration
    @Bean
    Serializer serializer() {
        return switch (serializerType.toLowerCase()) {
            case "json" -> new JsonSerializer();
            case "java" -> new ObjectSerializer();
            default -> throw new IllegalArgumentException("Unsupported serializer type: " + serializerType);
        };
    }
}
