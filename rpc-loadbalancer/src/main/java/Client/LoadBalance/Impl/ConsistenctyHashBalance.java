package Client.LoadBalance.Impl;

import Client.ServiceCenter.LoadBalance.LoadBalance;

import java.util.*;

public class ConsistenctyHashBalance implements LoadBalance {
    private static final int VIRTUAL_NODES = 5; // Number of virtual node;
    private SortedMap<Integer, String> hashCircle = new TreeMap<Integer, String>(); // <hash, virtual node name>
    private List<String> realNodes = new LinkedList<>(); // Real nodes, i.e., the actual service addresses
    private String[] servers = null;

    private void initBalancer(List<String> serverList) {
        for (String server : serverList) {
            realNodes.add(server);
            System.out.println("Adding real node: " + server);
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                String virtualNode = server + "&&VN" + i;
                int hash = getHash(virtualNode);
                hashCircle.put(hash, virtualNode);
                System.out.println("Adding virtual node: " + virtualNode + " with hash: " + hash);
            }
        }
    }

    public String getServer(String node, List<String> serverList) {
        initBalancer(serverList);
        int hash = getHash(node);
        Integer key = null;
        SortedMap<Integer, String> subMap = hashCircle.tailMap(hash);
        if (subMap.isEmpty()) {
            key = hashCircle.lastKey();
        } else {
            key = subMap.firstKey();
        }
        String virtualNode = hashCircle.get(key);
        return virtualNode.substring(0, virtualNode.indexOf("&&VN")); // Extract the real server address from the virtual node
    }

    // FNV1_32_HASH algorithm for hash calculation
    private static int getHash(String string) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < string.length(); i++) {
            hash = (hash ^ string.charAt(i)) * p;
            hash += hash << 13;
            hash ^= hash >> 7;
            hash += hash << 3;
            hash ^= hash >> 17;
            hash += hash << 5;
            if (hash < 0) {
                hash = Math.abs(hash);
            }
        }
        return hash;
    }

    @Override
    public void addNode(String node) {
        if (!realNodes.contains(node)) {
            realNodes.add(node);
            System.out.println("Adding new real node: " + node);
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                hashCircle.put(hash, virtualNode);
                System.out.println("Adding new virtual node: " + virtualNode + " with hash: " + hash);
            }
        }
    }

    @Override
    public void delNode(String node) {
        if (realNodes.contains(node)) {
            realNodes.remove(node);
            System.out.println("Removing real node: " + node);
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                if (hashCircle.containsKey(hash)) {
                    hashCircle.remove(hash);
                    System.out.println("Removing virtual node: " + virtualNode + " with hash: " + hash);
                } else {
                    System.out.println("Virtual node: " + virtualNode + " with hash: " + hash + " does not exist, cannot remove.");
                }
            }
        }
    }

    @Override
    public String balanceStrategy(List<String> addressList) {
        if (addressList == null || addressList.isEmpty()) {
            return null;
        }
        String random = UUID.randomUUID().toString(); // Generate a random string to use as the key for consistent hashing
        return getServer(random, addressList);
    }
}
