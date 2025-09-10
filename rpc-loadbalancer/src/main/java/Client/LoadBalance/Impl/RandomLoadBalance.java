package Client.LoadBalance.Impl;

import Client.ServiceCenter.LoadBalance.LoadBalance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {
    @Override
    public String balanceStrategy(List<String> serviceAddresses) {
        if (serviceAddresses == null || serviceAddresses.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int choice = random.nextInt(serviceAddresses.size());
        System.out.println("Using Random Load Balancing Strategy, selected service address: " + serviceAddresses.get(choice));
        return serviceAddresses.get(choice);
    }
    @Override
    public void addNode(String node) {

    }
    @Override
    public void delNode(String node) {

    }
}
