package com.rpc.provider.nacos;

import com.rpc.properties.RPCProperties;
import com.rpc.provider.AbstractServiceProvider;
import com.rpc.register.ServerRegister;
import extension.ExtensionLoader;
import org.springframework.stereotype.Component;

@Component
public class NacosServiceProvider extends AbstractServiceProvider {

    public NacosServiceProvider() {
        serverRegister = ExtensionLoader.getExtensionLoader(ServerRegister.class)
                .getExtension(RPCProperties.getRPCProperties().getRegister());
    }
}
