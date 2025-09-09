package Client.serviceCenter;

import Client.cache.ServiceCache;
import Client.serviceCenter.loadBalance.impl.ConsistenctyHashBalance;
import Client.serviceCenter.zkWatcher.ZookeeperWatch;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

public class ZookeeperServiceCenter implements ServiceCenter {
    private CuratorFramework client;
    private static final String ROOT_PATH = "MyRpc";
    private static final String RETRY = "CanRetry";
    private ServiceCache serviceCache;

    public ZookeeperServiceCenter() throws InterruptedException {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(retryPolicy)
                .namespace(ROOT_PATH)
                .build();
        this.client.start();
        System.out.println("Successfully connected to zookeeper!");

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
                System.out.println("No service found for: " + serviceName);
                return null;
            }
            String address = new ConsistenctyHashBalance().balanceStrategy(serviceAddresses);
            return parseAddress(address);
        } catch (Exception e) {
            System.out.println("Error during service discovery for: " + serviceName);
            e.printStackTrace();
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
                    System.out.println("Service " + serviceName + " is marked for retry whitelist, proceeding with retry.");
                    canRetry = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
