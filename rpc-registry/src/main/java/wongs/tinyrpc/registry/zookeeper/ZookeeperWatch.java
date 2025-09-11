package wongs.tinyrpc.registry.zookeeper;

import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.registry.cache.ServiceCache;
import lombok.AllArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

@Slf4j
@AllArgsConstructor
public class ZookeeperWatch {
    private CuratorFramework client;
    ServiceCache cache;

    public void initWatchService() throws InterruptedException {
        CuratorCache curatorCache = CuratorCache.build(client, "/");
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            @Override
            public void event(Type type, ChildData oldData, ChildData data) {
                switch (type.name()) {
                    case "NODE_CREATED" -> {
                        String[] pathList = parsePath(data);
                        if (pathList.length <= 2) {
                            log.info("{}", "Service created: " + data.getPath());
                        } else {
                            String serviceName = pathList[1];
                            String serviceAddress = pathList[2];
                            cache.addServiceToCache(serviceName, serviceAddress);
                            log.info("{}", "Service added to cache: " + serviceName + " -> " + serviceAddress);
                        }
                    }
                    case "NODE_CHANGED" -> {
                        if (oldData.getData() != null) {
                            log.info("{}", "cache data before change: " + new String(oldData.getData()));
                        } else {
                            log.info("{}", "first time change, no old data");
                        }
                        String[] oldPathList = parsePath(oldData);
                        String[] PathList = parsePath(data);
                        cache.replaceServiceAddress(oldPathList[1], oldPathList[2], PathList[2]);
                        log.info("{}", "Service address changed: " + oldPathList[1] + " from " + oldPathList[2] + " to " + PathList[2]);
                    }
                    case "NODE_DELETED" -> {
                        String[] pathList = parsePath(oldData);
                        if (pathList.length <= 2) {
                            break;
                        } else {
                            cache.deleteServiceAddressFromCache(pathList[1], pathList[2]);
                        }
                    }
                    default -> {
                        log.info("{}", "Unhandled event type: " + type.name());
                    }
                }
            }
        });
        curatorCache.start(); // Start the cache to begin listening for changes
    }

    private String[] parsePath(ChildData childData) {
        String path = childData.getPath();
        return path.split("/");
    }
}
