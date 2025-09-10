package Client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceCache {
    private static Map<String, List<String>> serviceCacheMap = new HashMap<>(); // Map to hold service names and their corresponding addresses

    public void addServiceToCache(String serviceName, String serviceAddress) {
        if (serviceCacheMap.containsKey(serviceName)) {
            List<String> addresses = serviceCacheMap.get(serviceName);
            addresses.add(serviceAddress);
            System.out.println("Service " + serviceName + " added to cache with address: " + serviceAddress);
        } else {
            List<String> addresses = new ArrayList<>();
            addresses.add(serviceAddress);
            serviceCacheMap.put(serviceName, addresses);
        }
    }

    public void replaceServiceAddress(String serviceName, String oldAddress, String newAddress) {
        if (serviceCacheMap.containsKey(serviceName)) {
            List<String> addresses = serviceCacheMap.get(serviceName);
            if (addresses.contains(oldAddress)) {
                addresses.remove(oldAddress);
                addresses.add(newAddress);
            } else {
                System.out.println("Old address not found for service " + serviceName + ": " + oldAddress);
            }
        } else {
            System.out.println("Service " + serviceName + " not found in cache.");
        }
    }

    public List<String> getServiceAddressesFromCache(String serviceName) {
        if (serviceCacheMap.containsKey(serviceName)) {
            return serviceCacheMap.get(serviceName);
        }
        return null;
    }

    public void deleteServiceAddressFromCache(String serviceName, String serviceAddress) {
        if (serviceCacheMap.containsKey(serviceName)) {
            List<String> addresses = serviceCacheMap.get(serviceName);
            addresses.remove(serviceAddress);
            System.out.println("Service " + serviceName + " removed from cache with address: " + serviceAddress);
        }
    }
}
