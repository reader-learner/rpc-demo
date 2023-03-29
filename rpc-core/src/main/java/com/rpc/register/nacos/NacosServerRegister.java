package com.rpc.register.nacos;

import com.rpc.register.AbstractServerRegister;
import com.rpc.register.ServerRegister;
import com.rpc.transport.dto.ServiceInstance;
import com.rpc.utils.NacosUtils;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServerRegister extends AbstractServerRegister implements ServerRegister {
    @Override
    public void registerService(String serverName, InetSocketAddress serviceAddress) {
        NacosUtils.registerServer(serverName, serviceAddress);
    }

    @Override
    public void clearServer(InetSocketAddress serviceAddress) {
        NacosUtils.deregisterInstance(serviceAddress);
    }

    @Override
    public List<ServiceInstance> getServerAllInstance(String serverName) {
        return NacosUtils.getServerAllInstances(serverName);
    }


}
