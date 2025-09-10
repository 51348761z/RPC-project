package wongs.tinyrpc.core.server.registry;

import java.net.InetSocketAddress;

public interface ServiceRegistry {
    void register(String serviceName, InetSocketAddress serviceAddress, boolean canRetry);
}
