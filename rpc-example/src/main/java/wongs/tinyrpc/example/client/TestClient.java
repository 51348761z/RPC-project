package wongs.tinyrpc.example.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import wongs.tinyrpc.example.config.RpcFramworkConfig;
import wongs.tinyrpc.core.client.proxy.ClientProxy;
import wongs.tinyrpc.common.dto.User;
import wongs.tinyrpc.common.service.UserService;

@Slf4j
public class TestClient {
    public static void main(String[] args) throws InterruptedException {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RpcFramworkConfig.class);
        ClientProxy clientProxy = context.getBean(ClientProxy.class);

        UserService proxy = clientProxy.getProxy(UserService.class);

        for (int i = 0; i < 120; i++) {
            Integer userId = i;
            if (i % 30 == 0) {
                Thread.sleep(10000);
            }
            new Thread(()->{
                try {
                    User user = proxy.getUserById(userId);
                    log.info("{}", "User ID: " + userId + ", User: " + user + " from thread: " + Thread.currentThread().getName());
                    Integer id = proxy.insertUserId(User.builder()
                            .id(userId)
                            .username("User" + userId.toString())
                            .sex(true)
                            .build());
                    log.info("{}", "Inserted User ID: " + id + " from thread: " + Thread.currentThread().getName());
                } catch (NullPointerException e) {
                    log.info("{}", "Service not available for User ID: " + userId + " from thread: " + Thread.currentThread().getName());
                } catch (Exception e) {
                    log.error("An error occurred", e);
                }
            }).start();
        }
    }
}
