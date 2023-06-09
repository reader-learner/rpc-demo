package com.rpc.discovery;

import com.rpc.transport.dto.ServiceInstance;
import extension.SPI;
import com.rpc.transport.dto.RPCRequest;

import java.net.InetSocketAddress;
import java.util.List;

@SPI
public interface ServerDiscovery {
    /**
     * 查找服务地址
     *
     * @param rpcRequest RPC 请求
     * @return 服务地址
     */
    InetSocketAddress lookupService(RPCRequest rpcRequest);

}
