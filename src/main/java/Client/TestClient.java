import Client.factory.RpcClientFactory;
import Client.proxy.ClientProxy;
import Client.rpcClient.RpcClient;
import common.pojo.User;
import common.service.UserService;

public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        ClientProxy clientProxy = new ClientProxy();
        UserService proxy = clientProxy.getProxy(UserService.class);

        for (int i = 0; i < 120; i++) {
            Integer userId = i;
            if (i % 30 == 0) {
                Thread.sleep(10000);
            }
            new Thread(()->{
                try {
                    User user = proxy.getUserById(userId);
                    System.out.println("User ID: " + userId + ", User: " + user + " from thread: " + Thread.currentThread().getName());
                    Integer id = proxy.insertUserId(User.builder()
                            .id(userId)
                            .username("User" + userId.toString())
                            .sex(true)
                            .build());
                    System.out.println("Inserted User ID: " + id + " from thread: " + Thread.currentThread().getName());
                } catch (NullPointerException e) {
                    System.out.println("Service not available for User ID: " + userId + " from thread: " + Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
