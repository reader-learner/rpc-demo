package com.rpc.loadbalance.impl;

import com.rpc.loadbalance.AbstractLoadBalancer;
import com.rpc.properties.RPCProperties;
import com.rpc.serialize.Serializer;
import com.rpc.transport.dto.RPCRequest;
import com.rpc.transport.dto.ServiceInstance;
import extension.ExtensionLoader;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;


public class ConsistentHashLoadBalancer extends AbstractLoadBalancer {
    private static final Map<java.lang.String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();
    protected final Serializer serializer;
    protected int replicaNumber = 160;

    {
        serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(RPCProperties.getRPCProperties().getSerialize());
    }


    @Override
    protected ServiceInstance doSelect(List<ServiceInstance> instances, RPCRequest request) {
        int identityHashCode = System.identityHashCode(instances);
        java.lang.String serviceName = request.getServer();
        ConsistentHashSelector hashSelector = selectors.get(serviceName);
        if (hashSelector == null || hashSelector.identityHashCode != identityHashCode) {
            hashSelector = new ConsistentHashSelector(instances, replicaNumber, identityHashCode);
            ConsistentHashSelector absent = selectors.putIfAbsent(serviceName, hashSelector);
            if (absent != null) {
                hashSelector = absent;
            }
        }

        return hashSelector.select(serviceName + Arrays.stream(request.getParameters()));
    }

    class ConsistentHashSelector {
        // 虚拟节点
        private final TreeMap<Long, String> virtualInvokers;

        private final int identityHashCode;

        ConsistentHashSelector(List<ServiceInstance> invokers, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            // 为每一个服务地址创建 replicaNumber 个虚拟节点
            for (ServiceInstance invoker : invokers) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    String s = new String(serializer.serialize(invoker), StandardCharsets.UTF_8);
                    byte[] digest = md5(s + i);
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        virtualInvokers.put(m, s);
                    }
                }
            }
        }

        byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            return md.digest();
        }

        long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24 | (long) (digest[2 + idx * 4] & 255) << 16 | (long) (digest[1 + idx * 4] & 255) << 8 | (long) (digest[idx * 4] & 255)) & 4294967295L;
        }

        public ServiceInstance select(String rpcServiceKey) {
            byte[] digest = md5(rpcServiceKey);
            return selectForKey(hash(digest, 0));
        }

        public ServiceInstance selectForKey(long hashCode) {
            // TreeMap 的 tailMap 可以取集合中大于传入值（hashCode）的子集
            // 在一致性哈希算法中可以理解为取比当前值大的第一个节点
            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();
            // 当前节点值是最大的，取第一个
            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }
            return serializer.deserialize(entry.getValue().getBytes(StandardCharsets.UTF_8), ServiceInstance.class);
        }
    }
}
