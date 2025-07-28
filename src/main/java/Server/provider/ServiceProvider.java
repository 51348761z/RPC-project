package Server.provider;

import Server.rateLimit.provider.RateLimitProvider;
import Server.serviceRegister.ServiceRegister;
import Server.serviceRegister.impl.ZookeeperServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServiceProvider {
    private Map<String, Object> localInterfaceProvider;
    private String host;
    private int port;
    private ServiceRegister serviceRegister;
    private RateLimitProvider rateLimitProvider;

    public ServiceProvider() {
        this("127.0.0.1", 9999);
    }
    public ServiceProvider(String host, int port) {
        this.host = host;
        this.port = port;
        this.localInterfaceProvider = new HashMap<>();
        this.serviceRegister = new ZookeeperServiceRegister();
        this.rateLimitProvider = new RateLimitProvider();
    }

    public void provideServiceInterface(Object service, boolean canRetry) {
        String serviceName = service.getClass().getName();
        Class<?>[] interfaceNames = service.getClass().getInterfaces();
        for (Class<?> interfaceName : interfaceNames) {
            localInterfaceProvider.put(interfaceName.getName(), service);
            serviceRegister.register(interfaceName.getName(), new InetSocketAddress(host, port), canRetry);
        }
    }

    public Object getService(String interfaceName) {
        return localInterfaceProvider.get(interfaceName);
    }

    public RateLimitProvider getRateLimitProvider() { return rateLimitProvider; }
}
