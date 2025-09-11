package wongs.tinyrpc.registry.zookeeper;

import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.balancer.RandomLoadBalancer;
import wongs.tinyrpc.core.client.discovery.ServiceDiscovery;
import wongs.tinyrpc.registry.cache.ServiceCache;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZookeeperServiceDiscovery implements ServiceDiscovery {
    private CuratorFramework client;
    private static final String ROOT_PATH = "MyRpc";
    private static final String RETRY = "CanRetry";
    private ServiceCache serviceCache;

    public ZookeeperServiceDiscovery() throws InterruptedException {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(retryPolicy)
                .namespace(ROOT_PATH)
                .build();
        this.client.start();
        log.info("{}", "Successfully connected to zookeeper!");

        // Initialize the service cache
        this.serviceCache = new ServiceCache();
        ZookeeperWatch watcher = new ZookeeperWatch(client, serviceCache);
        watcher.initWatchService();
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            // Check if the service is cached
            List<String> serviceAddresses = serviceCache.getServiceAddressesFromCache(serviceName);
            if (serviceAddresses == null) {
                serviceAddresses = client.getChildren().forPath("/" + serviceName);
            }
            if (serviceAddresses.isEmpty()) {
                log.info("{}", "No service found for: " + serviceName);
                return null;
            }
            String address = new RandomLoadBalancer().balanceStrategy(serviceAddresses);
            return parseAddress(address);
        } catch (Exception e) {
            log.info("{}", "Error during service discovery for: " + serviceName);
            log.error("An error occurred", e);
        }
        return null;
    }

    @Override
    public boolean checkRetry(String serviceName) {
        boolean canRetry = false;
        try {
            List<String> serviceList = client.getChildren().forPath("/" + RETRY);
            for (String service : serviceList) {
                if (service.equals(serviceName)) {
                    log.info("{}", "Service " + serviceName + " is marked for retry whitelist, proceeding with retry.");
                    canRetry = true;
                }
            }
        } catch (Exception e) {
            log.error("An error occurred", e);
        }
        return canRetry;
    }

    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() + ":" + serverAddress.getPort();
    }

    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}
