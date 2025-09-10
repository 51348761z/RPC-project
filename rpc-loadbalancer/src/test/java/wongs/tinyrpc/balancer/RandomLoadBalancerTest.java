package wongs.tinyrpc.balancer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomLoadBalancerTest {
    private RandomLoadBalancer loadBalancer;

    @BeforeEach
    public void setUp() {
        loadBalancer = new RandomLoadBalancer();
    }

    @Test
    @DisplayName("Test Random Load Balancer with multiple servers")
    public void testRandomLoadBalancer() {
        List<String> servers = Arrays.asList("Server1", "Server2", "Server3", "Server4", "Server5");
        String selectedServer = loadBalancer.balanceStrategy(servers);
        assertTrue(servers.contains(selectedServer));
    }

    @Test
    @DisplayName("Test Random Load Balancer with empty server list")
    public void testRandomLoadBalancerEmptyList() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            loadBalancer.balanceStrategy(List.of());
        });
    }

    @Test
    @DisplayName("Test Random Load Balancer with null server list")
    public void testRandomLoadBalancerNullList() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            loadBalancer.balanceStrategy(null);
        });
    }

    @Test
    @DisplayName("Test add server functionality")
    public void testAddServer() {
        loadBalancer.addNode("Server1");
        assertTrue(loadBalancer.getAddressList().contains("Server1"));
    }

    @Test
    @DisplayName("Test remove server functionality")
    public void testRemoveServer() {
        loadBalancer.addNode("Server1");
        loadBalancer.delNode("Server1");
        assertFalse(loadBalancer.getAddressList().contains("Server1"));
    }
}
