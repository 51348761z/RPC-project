package wongs.tinyrpc.core.client.balancer;

import java.util.List;

public interface LoadBalancer {
    String balanceStrategy(List<String> serviceAddresses);

    void addNode(String node);

    void delNode(String node);

    String getName();
}
