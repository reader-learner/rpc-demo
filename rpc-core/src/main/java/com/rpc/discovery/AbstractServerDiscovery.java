package com.rpc.discovery;

import com.rpc.cache.ServerDiscoveryCache;
import com.rpc.properties.RPCProperties;
import extension.ExtensionLoader;

public abstract class AbstractServerDiscovery implements ServerDiscovery{
    protected final ServerDiscoveryCache discoveryCache;

    {
        discoveryCache = ExtensionLoader.getExtensionLoader(ServerDiscoveryCache.class)
                .getExtension(RPCProperties.getRPCProperties().getDiscoverCache());
    }
}
