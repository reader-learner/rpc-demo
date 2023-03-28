package com.rpc.provider.nacos;

import com.rpc.properties.RPCProperties;
import com.rpc.provider.AbstractServiceProvider;
import com.rpc.register.ServerRegister;
import com.rpc.register.nacos.NacosServerRegister;
import extension.ExtensionLoader;

public class NacosServiceProvider extends AbstractServiceProvider {
    public NacosServiceProvider() {
        serverRegister = ExtensionLoader.getExtensionLoader(ServerRegister.class)
                .getExtension(RPCProperties.register);
    }
}
