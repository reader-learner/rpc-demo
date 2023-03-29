package com.rpc.transport;

import com.rpc.discovery.ServerDiscovery;
import com.rpc.properties.RPCProperties;
import extension.ExtensionLoader;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 客户端抽象类
 */

public abstract class AbstractRPCClient implements RPCClient {
    /**
     * 数据报ID生成
     */
    protected final AtomicInteger requestIdProvider = new AtomicInteger();
    /**
     * 服务发现
     */
    protected final ServerDiscovery serverDiscovery;

    public AbstractRPCClient() {
        serverDiscovery = ExtensionLoader.getExtensionLoader(ServerDiscovery.class).getExtension(RPCProperties.getRPCProperties().getDiscovery());
    }

}
