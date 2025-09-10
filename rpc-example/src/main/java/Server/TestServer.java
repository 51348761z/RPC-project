package Server;

import Server.RateLimit.Provider.Impl.RateLimitProviderImpl;
import Server.RateLimit.RateLimitProvider;
import Server.factory.RetryConfig;
import Server.provider.ServiceProvider;
import Server.server.RpcServer;
import Server.factory.RpcServerFactory;
import RpcCommon.service.UserService;
import Client.impl.UserServiceImpl;
import Server.serviceRegister.ServiceRegister;

public class TestServer {
    public static void main(String[] args) {
        String host = RpcServerFactory.getServerHost();
        int port = RpcServerFactory.getServerPort();
        UserService userService = new UserServiceImpl();
        ServiceRegister serviceRegister = new ZookeeperServiceRegister();
        RateLimitProvider rateLimitProvider = new RateLimitProviderImpl();

        ServiceProvider serviceProvider = new ServiceProvider(host, port, serviceRegister, rateLimitProvider);
        boolean retryEnabled = RetryConfig.isRetryEnabled(UserService.class.getName());
        serviceProvider.provideServiceInterface(userService, retryEnabled);

        RpcServer rpcServer = RpcServerFactory.createRpcServer(serviceProvider);
        System.out.println("Server is starting on " + host + ":" + port);
        rpcServer.start(port);
    }
}
