package com.rpc.discovery.nacos;

import com.rpc.discovery.AbstractServerDiscovery;
import com.rpc.discovery.ServerDiscovery;
import com.rpc.loadbalance.LoadBalancer;
import com.rpc.properties.RPCProperties;
import com.rpc.transport.dto.RPCRequest;
import com.rpc.transport.dto.ServiceInstance;
import enums.ErrorEnum;
import exception.RPCException;
import extension.ExtensionLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class NacosServerDiscovery extends AbstractServerDiscovery implements ServerDiscovery {

    private final LoadBalancer loadBalancer;


    public NacosServerDiscovery() {
        loadBalancer = ExtensionLoader.getExtensionLoader(LoadBalancer.class)
                .getExtension(RPCProperties.getRPCProperties().getLoadBalancer());
    }


    @Override
    public InetSocketAddress lookupService(RPCRequest rpcRequest) {
        String serverName = rpcRequest.getServer();
        List<ServiceInstance> allInstance = discoveryCache.getServiceInstances(serverName);
        if (CollectionUtils.isEmpty(allInstance)) {
            log.error("can't find server: [{}]", serverName);
            throw new RPCException(ErrorEnum.SERVER_CAN_NOT_BE_FOUND, serverName);
        }
        ServiceInstance serverAddress = loadBalancer.getServerAddress(allInstance, rpcRequest);


        return new InetSocketAddress(serverAddress.getHost(), serverAddress.getPort());
    }

    @Override
    public List<ServiceInstance> getServerAllInstance(String serverName) {
        return discoveryCache.getServiceInstances(serverName);
    }
}
