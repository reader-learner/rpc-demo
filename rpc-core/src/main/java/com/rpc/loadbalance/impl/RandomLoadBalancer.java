package com.rpc.loadbalance.impl;

import com.rpc.loadbalance.AbstractLoadBalancer;
import com.rpc.transport.dto.RPCRequest;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer extends AbstractLoadBalancer {
    @Override
    protected String doSelect(List<String> instances, RPCRequest request) {
        return instances.get(new Random().nextInt(instances.size()));
    }
}
