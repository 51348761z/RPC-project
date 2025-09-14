package wongs.tinyrpc.example.config;

import io.opentelemetry.api.trace.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
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
import wongs.tinyrpc.transport.serializer.Serializer;
import wongs.tinyrpc.transport.socket.SimpleRPCServer;

import java.util.ServiceLoader;

@Slf4j
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
        ServiceLoader<LoadBalancer> loader = ServiceLoader.load(LoadBalancer.class);
        String userChoice = loadBalancerStratergy.toLowerCase();
        for (LoadBalancer balancer : loader) {
            String balancerName = balancer.getName().toLowerCase();
            if (balancerName.equals(userChoice)) {
                log.info("{}", "SPI: Using load balancer: " + userChoice);
                return balancer;
            }
        }
        throw new IllegalArgumentException("Unsupported load balancer type: " + loadBalancerStratergy);
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
    ClientProxy clientProxy(RpcClient rpcClient, ServiceDiscovery serviceDiscovery, CircuitBreakerProvider circuitBreakerProvider, RetryStrategy retryStrategy, Tracer tracer) throws InterruptedException {
        return new ClientProxy(rpcClient, serviceDiscovery, circuitBreakerProvider, retryStrategy, tracer);
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
    RpcServer rpcServer(ServiceProvider serviceProvider, Serializer serializer, Tracer tracer) {
        return switch (serverType.toLowerCase()) {
            case "netty" -> new NettyRpcServer(serviceProvider, serializer, tracer);
            case "socket" -> new SimpleRPCServer(serviceProvider);
            default -> throw new IllegalArgumentException("Unsupported server type: " + serverType);
        };
    }

    // public components configuration
    @Bean
    Serializer serializer() {
        ServiceLoader<Serializer> loader = ServiceLoader.load(Serializer.class);
        String userChoice = serializerType.toLowerCase();

        for (Serializer serializer : loader) {
            String serializerName = serializer.getType().name().replace("_SERIALIZER", "").toLowerCase();
            if (serializerName.equals(userChoice)) {
                log.info("{}", "SPI: Using serializer: " + userChoice);
                return serializer;
            }
        }
        throw new IllegalArgumentException("Unsupported serializer type: " + serializerType);
    }
}
