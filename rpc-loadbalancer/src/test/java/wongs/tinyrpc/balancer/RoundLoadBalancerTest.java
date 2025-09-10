package wongs.tinyrpc.balancer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class RoundLoadBalancerTest {

    private RoundLoadBalancer loadBalancer;
    private List<String> addresses;

    @BeforeEach
    void setUp() {
        loadBalancer = new RoundLoadBalancer();
        addresses = Arrays.asList("127.0.0.1:8080", "127.0.0.1:8081", "127.0.0.1:8082");
    }

    @Test
    @DisplayName("Test that round robin returns addresses in order")
    void testRoundRobinReturnsAddressesInOrder() {
        Assertions.assertEquals("127.0.0.1:8080", loadBalancer.balanceStrategy(addresses));
        Assertions.assertEquals("127.0.0.1:8081", loadBalancer.balanceStrategy(addresses));
        Assertions.assertEquals("127.0.0.1:8082", loadBalancer.balanceStrategy(addresses));
        Assertions.assertEquals("127.0.0.1:8080", loadBalancer.balanceStrategy(addresses));
    }

    @Test
    @DisplayName("Test that single address always returns the same address")
    void testRoundRobinWithSingleAddress() {
        List<String> single = Collections.singletonList("192.168.1.1:9000");
        Assertions.assertEquals("192.168.1.1:9000", loadBalancer.balanceStrategy(single));
        Assertions.assertEquals("192.168.1.1:9000", loadBalancer.balanceStrategy(single));
    }

    @Test
    @DisplayName("Test that empty address list throws IllegalArgumentException")
    void testEmptyAddressListThrowsException() {
        List<String> empty = Collections.emptyList();
        Assertions.assertThrows(IllegalArgumentException.class, () -> loadBalancer.balanceStrategy(empty));
    }

    @Test
    @DisplayName("Test that null address list throws IllegalArgumentException")
    void testNullAddressListThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> loadBalancer.balanceStrategy(null));
    }

    @Test
    @DisplayName("Test that changing the address list resets the round-robin sequence")
    void testAddressesChangeResetsRoundRobin() {
        Assertions.assertEquals("127.0.0.1:8080", loadBalancer.balanceStrategy(addresses));
        List<String> newAddresses = Arrays.asList("10.0.0.1:7000", "10.0.0.2:7001");
        Assertions.assertEquals("10.0.0.1:7000", loadBalancer.balanceStrategy(newAddresses));
        Assertions.assertEquals("10.0.0.2:7001", loadBalancer.balanceStrategy(newAddresses));
        Assertions.assertEquals("10.0.0.1:7000", loadBalancer.balanceStrategy(newAddresses));
    }

    @Test
    @DisplayName("Test addNode adds a new node")
    void testAddNode() {
        loadBalancer.addNode("192.168.1.100:9000");
        Assertions.assertTrue(loadBalancer.getAddresses().contains("192.168.1.100:9000"));
    }

    @Test
    @DisplayName("Test addNode does not add duplicate node")
    void testAddDuplicateNode() {
        loadBalancer.addNode("192.168.1.101:9001");
        int sizeBefore = loadBalancer.getAddresses().size();
        loadBalancer.addNode("192.168.1.101:9001");
        int sizeAfter = loadBalancer.getAddresses().size();
        Assertions.assertEquals(sizeBefore, sizeAfter);
    }

    @Test
    @DisplayName("Test delNode removes an existing node")
    void testDelNode() {
        loadBalancer.addNode("192.168.1.102:9002");
        Assertions.assertTrue(loadBalancer.getAddresses().contains("192.168.1.102:9002"));
        loadBalancer.delNode("192.168.1.102:9002");
        Assertions.assertFalse(loadBalancer.getAddresses().contains("192.168.1.102:9002"));
    }

    @Test
    @DisplayName("Test delNode does nothing if node does not exist")
    void testDelNonExistentNode() {
        int sizeBefore = loadBalancer.getAddresses().size();
        loadBalancer.delNode("not.exist:9999");
        int sizeAfter = loadBalancer.getAddresses().size();
        Assertions.assertEquals(sizeBefore, sizeAfter);
    }

    @Test
    @DisplayName("Test addNode with null does nothing")
    void testAddNullNode() {
        int sizeBefore = loadBalancer.getAddresses().size();
        loadBalancer.addNode(null);
        int sizeAfter = loadBalancer.getAddresses().size();
        Assertions.assertEquals(sizeBefore, sizeAfter);
    }
}
