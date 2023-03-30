package com.rpc.cache;

import com.rpc.properties.RPCProperties;
import com.rpc.register.ServerRegister;
import extension.ExtensionLoader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AbstractServerDiscoveryCache {
    protected final Long deleteTime = 10000L;
    protected final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(); // 定时任务线程池
    protected final ServerRegister register; // 注册中心
    protected final ExecutorService executor = Executors.newCachedThreadPool(); // 异步更新线程池

    {
        register = ExtensionLoader.getExtensionLoader(ServerRegister.class)
                .getExtension(RPCProperties.getRPCProperties().getRegister());
    }
}
