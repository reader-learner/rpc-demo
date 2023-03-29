package com.rpc.discovery;

import com.rpc.cache.ServerDiscoveryCache;
import com.rpc.loadbalance.LoadBalancer;
import com.rpc.properties.RPCProperties;
import extension.ExtensionLoader;

public abstract class AbstractServerDiscovery implements ServerDiscovery {

    protected final LoadBalancer loadBalancer;
    protected final ServerDiscoveryCache discoveryCache;

    {
        discoveryCache = ExtensionLoader.getExtensionLoader(ServerDiscoveryCache.class)
                .getExtension(RPCProperties.getRPCProperties().getDiscoverCache());
        loadBalancer = ExtensionLoader.getExtensionLoader(LoadBalancer.class)
                .getExtension(RPCProperties.getRPCProperties().getLoadBalancer());
    }


}
