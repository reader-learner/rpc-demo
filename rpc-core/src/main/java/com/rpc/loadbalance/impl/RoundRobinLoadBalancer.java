package com.rpc.loadbalance.impl;

import com.rpc.loadbalance.AbstractLoadBalancer;
import com.rpc.transport.dto.RPCRequest;
import com.rpc.transport.dto.ServiceInstance;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer extends AbstractLoadBalancer {
    private static final Map<String, AtomicInteger> serverCyclicCounters = new ConcurrentHashMap<>();

    @Override
    protected ServiceInstance doSelect(List<ServiceInstance> instances, RPCRequest request) {
        String serviceName = request.getServer();
        AtomicInteger useTimes = serverCyclicCounters.getOrDefault(serviceName, null);
        if (useTimes == null) {
            useTimes = new AtomicInteger(-1);
            AtomicInteger existingValue = serverCyclicCounters.putIfAbsent(serviceName, useTimes);
            if (existingValue != null) {
                useTimes = existingValue;
            }
        }
        return instances.get(incrementAndGetIndex(instances.size(), useTimes));
    }

    private int incrementAndGetIndex(int instancesSize, AtomicInteger atomicInteger) {
        return Math.abs(atomicInteger.incrementAndGet()) % instancesSize;
    }
}
