package com.rpc.register.nacos;

import com.rpc.register.ServerRegister;
import com.rpc.utils.NacosUtils;

import java.net.InetSocketAddress;

public class NacosServerRegister implements ServerRegister {
    @Override
    public void registerService(String serverName, InetSocketAddress serviceAddress) {
        NacosUtils.registerServer(serverName, serviceAddress);
    }

    @Override
    public void clearServer(InetSocketAddress serviceAddress) {
        NacosUtils.deregisterInstance(serviceAddress);
    }
}
