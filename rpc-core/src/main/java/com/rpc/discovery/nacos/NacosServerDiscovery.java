package com.rpc.discovery.nacos;

import com.rpc.discovery.AbstractServerDiscovery;
import com.rpc.discovery.ServerDiscovery;
import com.rpc.register.AbstractServerRegister;
import com.rpc.transport.dto.RPCRequest;
import com.rpc.transport.dto.ServiceInstance;
import enums.ErrorEnum;
import exception.RPCException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class NacosServerDiscovery extends AbstractServerDiscovery implements ServerDiscovery {


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


}
