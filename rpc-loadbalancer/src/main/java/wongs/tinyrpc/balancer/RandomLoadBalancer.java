package wongs.tinyrpc.balancer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.core.client.balancer.LoadBalancer;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class RandomLoadBalancer implements LoadBalancer {

    @Getter
    private final List<String> addressList = new CopyOnWriteArrayList<>();

    @Override
    public String balanceStrategy(List<String> serviceAddresses) {
        // Update the internal address list if there are changes
        if (!Objects.equals(this.addressList, serviceAddresses)) {
            this.addressList.clear();
            if (serviceAddresses != null) {
                this.addressList.addAll(serviceAddresses);
            }
        }

        if (this.addressList.isEmpty()) {
            log.warn("No available service addresses to balance.");
            throw new IllegalArgumentException("No available service addresses");
        }
        Random random = new Random();
        int choice = random.nextInt(serviceAddresses.size());
        String selectedAddress = serviceAddresses.get(choice);
        log.info("Randomly selected service address: {}", selectedAddress);
        return selectedAddress;
    }
    @Override
    public void addNode(String node) {
        if (node != null && !this.addressList.contains(node)) {
            this.addressList.add(node);
            log.info("Node added: {}", node);
        }
    }
    @Override
    public void delNode(String node) {
        if (node != null && this.addressList.contains(node)) {
            addressList.remove(node);
            log.info("Node removed: {}", node);
        }
        log.info("Node not found: {}", node);
    }

    @Override
    public String getName() {
        return "Random";
    }
}
