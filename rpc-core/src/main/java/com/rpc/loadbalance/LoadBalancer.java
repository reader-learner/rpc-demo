package com.rpc.loadbalance;

import com.rpc.transport.dto.RPCRequest;
import com.rpc.transport.dto.ServiceInstance;
import extension.SPI;

import java.util.List;

@SPI
public interface LoadBalancer {
    ServiceInstance getServerAddress(List<ServiceInstance> instances, RPCRequest request);

}
