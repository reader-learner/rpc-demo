package com.rpc.loadbalance;

import com.rpc.transport.dto.RPCRequest;
import org.springframework.util.CollectionUtils;

import java.util.List;

public abstract class AbstractLoadBalancer implements LoadBalancer {
    @Override
    public String getServerAddress(List<String> instances, RPCRequest request) {
        if (CollectionUtils.isEmpty(instances)) {
            return null;
        }
        if (instances.size() == 1) {
            return instances.get(0);
        }

        return doSelect(instances, request);
    }

    protected abstract String doSelect(List<String> instances, RPCRequest request);
}
