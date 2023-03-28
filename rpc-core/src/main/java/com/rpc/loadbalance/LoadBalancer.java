package com.rpc.loadbalance;

import com.rpc.transport.dto.RPCRequest;
import extension.SPI;

import java.util.List;

@SPI
public interface LoadBalancer {
    String getServerAddress(List<String> instances, RPCRequest request);

}
