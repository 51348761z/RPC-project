package wongs.tinyrpc.balancer;

import wongs.tinyrpc.core.client.balancer.LoadBalance;

import java.util.List;

public class RoundLoadBalance implements LoadBalance {
    private int choice = -1;

    @Override
    public String balanceStrategy(List<String> serviceAddresses) {
        if (serviceAddresses == null || serviceAddresses.isEmpty()) {
            return null;
        }
        choice = (choice + 1) % serviceAddresses.size();
        System.out.println("Using Round Robin Load Balancing Strategy, current choice: " + choice + ", service address: " + serviceAddresses.get(choice));
        return serviceAddresses.get(choice);
    }

    @Override
    public void addNode(String node) {
    }

    @Override
    public void delNode(String node) {
    }
}
