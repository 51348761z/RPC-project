package wongs.tinyrpc.balancer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.core.client.balancer.LoadBalancer;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RoundRobinLoadBalancer implements LoadBalancer {
    private AtomicInteger choice = new AtomicInteger(0);
    @Getter
    private List<String> addresses = new CopyOnWriteArrayList<>();

    @Override
    public String balanceStrategy(List<String> serviceAddresses) {
        if (serviceAddresses == null || serviceAddresses.isEmpty()) {
            throw new IllegalArgumentException("No service address available");
        }
        if (!Objects.equals(this.addresses, serviceAddresses)) {
            this.addresses.clear();
            this.addresses.addAll(serviceAddresses);
            choice.set(0);
        }
        if (this.addresses.isEmpty()) {
            throw new IllegalArgumentException("No service address available");
        }
        int currentChoice = choice.getAndUpdate(i -> (i + 1) % addresses.size());
        String SelectedAddress = serviceAddresses.get(currentChoice);
        log.info("Using Round Robin Load Balancing Strategy, current choice: {}, service address: {}", currentChoice, SelectedAddress);
        return SelectedAddress;
    }

    @Override
    public void addNode(String node) {
        if (node != null && !this.addresses.contains(node)) {
            this.addresses.add(node);
            log.info("Node added: {}", node);
        }
    }

    @Override
    public void delNode(String node) {
        if (!this.addresses.isEmpty()&& this.addresses.contains(node)) {
            this.addresses.remove(node);
            log.info("Node removed: {}", node);
        }
    }

    @Override
    public String getName() {
        return "RoundRobin";
    }
}
