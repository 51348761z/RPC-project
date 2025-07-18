import Server.provider.ServiceProvider;
import Server.server.RpcServer;
import Server.factory.RpcServerFactory;
import common.service.UserService;
import common.service.impl.UserServiceImpl;

public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer = RpcServerFactory.createRpcServer(serviceProvider);
        var port = RpcServerFactory.getServerPort();
        System.out.println("Server started on port " + port);
        rpcServer.start(port);
    }
}
