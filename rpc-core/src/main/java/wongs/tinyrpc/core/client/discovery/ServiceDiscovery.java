package wongs.tinyrpc.core.client.discovery;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    InetSocketAddress serviceDiscovery(String serviceName);
    boolean checkRetry(String serviceName);
}
