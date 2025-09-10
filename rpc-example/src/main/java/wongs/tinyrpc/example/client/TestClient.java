package wongs.tinyrpc.example.client;

import wongs.tinyrpc.core.client.breaker.CircuitBreakerProvider;
import wongs.tinyrpc.fault.breaker.CircuitBreakerProviderImpl;
import wongs.tinyrpc.core.client.retry.RetryStrategy;
import wongs.tinyrpc.registry.zookeeper.ZookeeperServiceDiscovery;
import wongs.tinyrpc.core.client.discovery.ServiceDiscovery;
import wongs.tinyrpc.transport.netty.client.NettyRpcClient;
import wongs.tinyrpc.core.client.proxy.ClientProxy;
import wongs.tinyrpc.core.client.transport.RpcClient;
import wongs.tinyrpc.fault.retry.GuavaRetryStrategy;
import wongs.tinyrpc.common.dto.User;
import wongs.tinyrpc.common.service.UserService;

public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        // construct client proxy
        ServiceDiscovery serviceDiscovery = new ZookeeperServiceDiscovery();
        RpcClient rpcClient = new NettyRpcClient(serviceDiscovery);
        CircuitBreakerProvider circuitBreakerProvider = new CircuitBreakerProviderImpl();
        RetryStrategy retryStrategy = new GuavaRetryStrategy();
        ClientProxy clientProxy = new ClientProxy(rpcClient, serviceDiscovery, circuitBreakerProvider, retryStrategy);

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
