package com.rpc.transport.netty.work;

import com.rpc.provider.ServiceProvider;
import com.rpc.transport.dto.RPCRequest;
import enums.ErrorEnum;
import exception.RPCException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

@Slf4j
public class WorkHandler implements Runnable, Supplier<Object> {
    private final RPCRequest rpcRequest;
    private final ServiceProvider serviceProvider;

    public WorkHandler(RPCRequest rpcRequest, ServiceProvider serviceProvider) {
        this.rpcRequest = rpcRequest;
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void run() {

    }

    private Object callService(RPCRequest rpcRequest) {
        Object res;
        try {
            String serviceName = rpcRequest.getServiceName();
            Object providerService = serviceProvider.getService(serviceName);
            Method method = providerService.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            res = method.invoke(providerService, rpcRequest.getParameters());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RPCException(ErrorEnum.CALL_SERVICE_ERROR, e.getMessage());
        }
        return res;
    }

    @Override
    public Object get() {
        return callService(rpcRequest);
    }
}
