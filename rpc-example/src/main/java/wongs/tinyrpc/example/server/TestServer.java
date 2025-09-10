package wongs.tinyrpc.example.server;

import wongs.tinyrpc.limiter.RateLimitProviderImpl;
import wongs.tinyrpc.core.server.ratelimit.RateLimitProvider;
import wongs.tinyrpc.example.factory.RetryConfig;
import wongs.tinyrpc.core.server.provider.ServiceProvider;
import wongs.tinyrpc.core.server.transport.RpcServer;
import wongs.tinyrpc.example.factory.RpcServerFactory;
import wongs.tinyrpc.common.service.UserService;
import wongs.tinyrpc.example.service.UserServiceImpl;
import wongs.tinyrpc.core.server.registry.ServiceRegistry;
import wongs.tinyrpc.registry.zookeeper.ZookeeperServiceRegistry;

public class TestServer {
    public static void main(String[] args) {
        String host = RpcServerFactory.getServerHost();
        int port = RpcServerFactory.getServerPort();
        UserService userService = new UserServiceImpl();
        ServiceRegistry serviceRegistry = new ZookeeperServiceRegistry();
        RateLimitProvider rateLimitProvider = new RateLimitProviderImpl();

        ServiceProvider serviceProvider = new ServiceProvider(host, port, serviceRegistry, rateLimitProvider);
        boolean retryEnabled = RetryConfig.isRetryEnabled(UserService.class.getName());
        serviceProvider.provideServiceInterface(userService, retryEnabled);

        RpcServer rpcServer = RpcServerFactory.createRpcServer(serviceProvider);
        System.out.println("Server is starting on " + host + ":" + port);
        rpcServer.start(port);
    }
}
