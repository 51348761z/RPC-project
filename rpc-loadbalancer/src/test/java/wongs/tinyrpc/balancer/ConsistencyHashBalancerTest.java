package wongs.tinyrpc.balancer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConsistencyHashBalancerTest {
    private ConsistenctyHashBalancer balancer;

    @BeforeEach
    public void setUp() {
        balancer = new ConsistenctyHashBalancer();
    }

    @Test
    @DisplayName("Test initBalancer method")
    public void testInitBalancer() {
        List<String> nodes = Arrays.asList("server1", "server2", "server3");
        balancer.initBalancer(nodes);
        assertTrue(!balancer.getHashRing().isEmpty(), "Hash ring should not be empty");
        assertTrue(balancer.getRealNodes().containsAll(nodes), "Real nodes should contain all initial nodes");
    }

    @Test
    @DisplayName("Test getServer method")
    public void testGetServer() {
        List<String> nodes = Arrays.asList("server1", "server2", "server3");
        balancer.initBalancer(nodes);

        String server = balancer.getServer("request-1", nodes);
        assertNotNull(server, "Server should not be null");
        assertTrue(nodes.contains(server), "Server should be one of the initial nodes");

        String server2 = balancer.getServer("request-2", nodes);
        assertNotEquals(server, server2, "Servers for different requests should be different");
    }

    @Test
    @DisplayName("Test addNode method")
    public void testAddNode() {
        List<String> nodes = Arrays.asList("server1", "server2");
        balancer.initBalancer(nodes);
        balancer.addNode("server3");

        assertTrue(balancer.getRealNodes().contains("server3"), "Real nodes should contain the added node");
        assertTrue(balancer.getHashRing().size() > 2, "Hash ring should contain the added node");
    }

    @Test
    @DisplayName("Test removeNode method")
    public void testRemoveNode() {
        List<String> nodes = Arrays.asList("server1", "server2", "server3");
        balancer.initBalancer(nodes);

        balancer.delNode("server1");

        assertFalse(balancer.getRealNodes().contains("server1"), "Real nodes should not contain the removed node");
        assertFalse(balancer.getHashRing().values().stream().anyMatch(s -> s.startsWith("server1")), "Hash ring should not contain the removed node");
    }

    @Test
    @DisplayName("Test balanceStrategy with empty nodes list")
    public void testBalanceStrategyWithEmptyNodes() {
        List<String> nodes = Arrays.asList();
        assertThrows(IllegalArgumentException.class, () -> {
            balancer.balanceStrategy(nodes);
        });
    }

    @Test
    @DisplayName("Test balanceStrategy with null nodes list")
    public void testBalanceStrategyWithNullNodes() {
        assertThrows(IllegalArgumentException.class, () -> {
            balancer.balanceStrategy(null);
        });
    }

    @Test
    @DisplayName("Test number of virtual nodes")
    public void testNumberOfVirtualNodes() {
        assertEquals(ConsistenctyHashBalancer.getVIRTUAL_NODES(), 5, "Number of virtual nodes should be 5");
    }
}
