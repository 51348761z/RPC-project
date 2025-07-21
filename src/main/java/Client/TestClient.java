import Client.factory.RpcClientFactory;
import Client.proxy.ClientProxy;
import Client.rpcClient.RpcClient;
import common.pojo.User;
import common.service.UserService;

public class TestClient {
    public static void main(String[] args) {
        ClientProxy clientProxy = new ClientProxy();
        UserService userServiceNetty = clientProxy.getProxy(UserService.class);

        User user = userServiceNetty.getUserById(1);
        User u = User.builder()
                .id(100)
                .username("xxx")
                .sex(true)
                .build();
        Integer id = userServiceNetty.insertUserId(u);
        System.out.println("--- Using Netty Client Proxy ---");

    }
}
