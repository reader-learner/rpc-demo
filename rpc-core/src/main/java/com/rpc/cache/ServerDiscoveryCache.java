package com.rpc.cache;

import com.rpc.transport.dto.ServiceInstance;
import extension.SPI;

import java.util.List;

@SPI
public interface ServerDiscoveryCache {
    List<ServiceInstance> getServiceInstances(String serverName);
}
