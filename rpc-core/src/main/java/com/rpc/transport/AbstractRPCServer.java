package com.rpc.transport;

import com.rpc.config.ServiceConfig;
import com.rpc.properties.RPCProperties;
import com.rpc.provider.ServiceProvider;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractRPCServer implements RPCServer {

    /**
     * 存放扫描到的标注 @RpcService 的服务
     */
    public static Set<ServiceConfig> serviceConfigs;
    protected String host;
    protected int port;
    protected ServiceProvider serviceProvider;

    {
        serviceConfigs = new HashSet<>();
        port = RPCProperties.getRPCProperties().getPort();
        host = RPCProperties.getRPCProperties().getHost();
    }

    @Override
    public void publishService(ServiceConfig config) {
        serviceProvider.addService(config.getServiceName(), config.getService(), new InetSocketAddress(host, port));
    }
}
