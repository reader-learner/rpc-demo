package com.rpc.loadbalance;

import com.rpc.transport.dto.RPCRequest;
import com.rpc.transport.dto.ServiceInstance;
import org.springframework.util.CollectionUtils;

import java.util.List;

public abstract class AbstractLoadBalancer implements LoadBalancer {
    @Override
    public ServiceInstance getServerAddress(List<ServiceInstance> instances, RPCRequest request) {
        if (CollectionUtils.isEmpty(instances)) {
            return null;
        }
        if (instances.size() == 1) {
            return instances.get(0);
        }

        return doSelect(instances, request);
    }

    protected abstract ServiceInstance doSelect(List<ServiceInstance> instances, RPCRequest request);
}
