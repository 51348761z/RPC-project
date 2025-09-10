package wongs.tinyrpc.core.server.provider;

import wongs.tinyrpc.core.server.ratelimit.RateLimitProvider;
import wongs.tinyrpc.core.server.registry.ServiceRegistry;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServiceProvider {
    private Map<String, Object> localInterfaceProvider;
    private String host;
    private int port;
    private ServiceRegistry serviceRegistry;
    private RateLimitProvider rateLimitProvider;

    public ServiceProvider(String host, int port, ServiceRegistry serviceRegistry, RateLimitProvider rateLimitProvider) {
        this.host = host;
        this.port = port;
        this.localInterfaceProvider = new HashMap<>();
        this.serviceRegistry = serviceRegistry;
        this.rateLimitProvider = rateLimitProvider;
    }

    public void provideServiceInterface(Object service, boolean canRetry) {
        String serviceName = service.getClass().getName();
        Class<?>[] interfaceNames = service.getClass().getInterfaces();
        for (Class<?> interfaceName : interfaceNames) {
            localInterfaceProvider.put(interfaceName.getName(), service);
            serviceRegistry.register(interfaceName.getName(), new InetSocketAddress(host, port), canRetry);
        }
    }

    public Object getService(String interfaceName) {
        return localInterfaceProvider.get(interfaceName);
    }

    public RateLimitProvider getRateLimitProvider() { return rateLimitProvider; }
}
