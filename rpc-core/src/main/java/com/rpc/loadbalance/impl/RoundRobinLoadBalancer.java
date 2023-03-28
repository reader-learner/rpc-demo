package com.rpc.loadbalance.impl;

import com.rpc.loadbalance.AbstractLoadBalancer;
import com.rpc.transport.dto.RPCRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer extends AbstractLoadBalancer {
    private static final Map<String, AtomicInteger> serverCyclicCounters = new ConcurrentHashMap<>();

    @Override
    protected String doSelect(List<String> instances, RPCRequest request) {
        String serviceName = request.getServiceName();
        AtomicInteger atomicInteger = serverCyclicCounters.get(serviceName);
        if (atomicInteger == null) {
            atomicInteger = new AtomicInteger(-1);
            AtomicInteger existingValue = serverCyclicCounters.putIfAbsent(serviceName, atomicInteger);
            if (existingValue != null) {
                atomicInteger = existingValue;
            }
        }
        return instances.get(incrementAndGetIndex(instances.size(), atomicInteger));
    }

    private int incrementAndGetIndex(int instancesSize, AtomicInteger atomicInteger) {
        return Math.abs(atomicInteger.incrementAndGet()) % instancesSize;
    }
}
