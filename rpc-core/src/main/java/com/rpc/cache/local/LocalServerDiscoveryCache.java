package com.rpc.cache.local;

import com.rpc.cache.AbstractServerDiscoveryCache;
import com.rpc.cache.ServerDiscoveryCache;
import com.rpc.transport.dto.ServiceInstance;
import enums.ErrorEnum;
import exception.RPCException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LocalServerDiscoveryCache extends AbstractServerDiscoveryCache implements ServerDiscoveryCache {
    private final Map<String, List<ServiceInstance>> serverCache = new ConcurrentHashMap<>(); // 本地缓存

    private final Map<ServiceInstance, Long> expirations = new ConcurrentHashMap<>(); // 服务实例过期时间


    public LocalServerDiscoveryCache() {
        // 定时清理过期的服务实例
        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            for (ServiceInstance instance : expirations.keySet()) {
                if (expirations.get(instance) < now) {
                    serverCache.get(instance.getServerName()).remove(instance);
                }
            }
        }, 5, 30, TimeUnit.SECONDS); // 每隔 30 秒清理一次过期的服务实例

    }

    @Override
    public List<ServiceInstance> getServiceInstances(String serverName) {

        List<ServiceInstance> serviceInstances = serverCache.get(serverName);

        if (CollectionUtils.isEmpty(serviceInstances)) {
            List<ServiceInstance> allInstance = register.getServerAllInstance(serverName);
            if (allInstance.isEmpty()) throw new RPCException(ErrorEnum.SERVER_CAN_NOT_BE_FOUND);

            CompletableFuture.runAsync(() -> {
                serverCache.putIfAbsent(serverName, new CopyOnWriteArrayList<>(allInstance));
                for (ServiceInstance serviceInstance : allInstance) {
                    expirations.put(serviceInstance, System.currentTimeMillis() + deleteTime);
                }
            }, executor).exceptionally((e) -> {
                log.error("服务发现本地缓存更新异常", e);
                return null;
            });

            serviceInstances = allInstance;
        } else {
            serviceInstances.removeIf(serviceInstance -> expirations.get(serviceInstance) < System.currentTimeMillis());
            if (serviceInstances.isEmpty()) return getServiceInstances(serverName);
        }
        return serviceInstances;
    }


}
