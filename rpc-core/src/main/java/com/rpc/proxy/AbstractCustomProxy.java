package com.rpc.proxy;

import com.rpc.config.ServiceConfig;
import com.rpc.transport.RPCClient;
import com.rpc.transport.dto.RPCRequest;
import com.rpc.transport.dto.RPCResponse;
import enums.ErrorEnum;
import enums.ResponseCodeEnum;
import exception.RPCException;

import java.lang.reflect.Method;
import java.util.UUID;


public abstract class AbstractCustomProxy implements CustomProxy {
    /**
     * Rpc 客户端
     */
    protected RPCClient rpcClient;
    /**
     * 服务信息
     */
    protected ServiceConfig config;

    public AbstractCustomProxy() {
    }

    public AbstractCustomProxy(RPCClient rpcClient, ServiceConfig config) {
        this.rpcClient = rpcClient;
        this.config = config;
    }

    @Override
    public void setParams(RPCClient rpcClient, ServiceConfig config) {
        this.rpcClient = rpcClient;
        this.config = config;
    }

    protected Object sendRequestAndGetResponse(Method method, Object[] args) {
        RPCRequest rpcRequest = RPCRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .parameters(args)
                .group(config.getGroup())
                .version(config.getVersion())
                .build();
        RPCResponse<Object> rpcResponse = rpcClient.sendRequest(rpcRequest);
        checkForResponse(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }

    protected void checkForResponse(RPCRequest rpcRequest, RPCResponse<Object> rpcResponse) {
        // 远程调用失败
        if (rpcResponse == null) {
            throw new RPCException(ErrorEnum.RPC_INVOCATION_FAILURE, String.format("service interface: [%s]", rpcRequest.getInterfaceName()));
        }
        // 请求和响应的ID不一致
        if (!(rpcRequest.getRequestId().equals(rpcResponse.getRequestId()))) {
            throw new RPCException(ErrorEnum.REQUEST_NOT_MATCH_RESPONSE, String.format("service interface: [%s]", rpcRequest.getInterfaceName()));
        }
        // 远程调用失败
        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCodeEnum.SUCCESS.getCode())) {
            throw new RPCException(ErrorEnum.RPC_INVOCATION_FAILURE, String.format("service interface: [%s]", rpcRequest.getInterfaceName()));
        }
    }
}
