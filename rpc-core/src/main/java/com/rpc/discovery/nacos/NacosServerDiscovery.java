package com.rpc.discovery.nacos;

import com.rpc.discovery.ServerDiscovery;
import com.rpc.loadbalance.LoadBalancer;
import com.rpc.properties.RPCProperties;
import com.rpc.transport.dto.RPCRequest;
import com.rpc.utils.NacosUtils;
import enums.ErrorEnum;
import exception.RPCException;
import extension.ExtensionLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class NacosServerDiscovery implements ServerDiscovery {

    private final LoadBalancer loadBalancer;

    {
        loadBalancer = ExtensionLoader.getExtensionLoader(LoadBalancer.class)
                .getExtension(RPCProperties.loadBalancer);
    }

    @Override
    public InetSocketAddress lookupService(RPCRequest rpcRequest) {
        String serviceName = rpcRequest.getServiceName();
        List<String> allInstance = NacosUtils.getAllInstance(serviceName);
        if (CollectionUtils.isEmpty(allInstance)) {
            log.error("can't find service: [{}]", serviceName);
            throw new RPCException(ErrorEnum.SERVICE_CAN_NOT_BE_FOUND, serviceName);
        }
        String serverAddress = loadBalancer.getServerAddress(allInstance, rpcRequest);
        String[] split = serverAddress.split(":");

        return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
    }
}
