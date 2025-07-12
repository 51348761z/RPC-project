import Client.proxy.ClientProxy;
import common.pojo.User;
import common.service.UserService;

public class TestClient {
    public static void main(String[] args) {
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 9999);
        UserService proxy = clientProxy.getProxy(UserService.class);

        User user = proxy.getUserById(1);
        System.out.println("user from server = " + user.toString());

        User u = User.builder().id(100).username("xxx").sex(true).build();
        Integer id = proxy.insertUserId(u);
        System.out.println("inserted user id to server = " + id);
    }
}
