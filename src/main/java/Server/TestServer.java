import Server.provider.ServiceProvider;
import Server.server.RpcServer;
import Server.factory.RpcServerFactory;
import common.service.UserService;
import common.service.impl.UserServiceImpl;

public class TestServer {
    public static void main(String[] args) {
        String host = RpcServerFactory.getServerHost();
        int port = RpcServerFactory.getServerPort();
        UserService userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider(host, port);
        serviceProvider.provideServiceInterface(userService, true);

        RpcServer rpcServer = RpcServerFactory.createRpcServer(serviceProvider);
        System.out.println("Server is starting on " + host + ":" + port);
        rpcServer.start(port);
    }
}
