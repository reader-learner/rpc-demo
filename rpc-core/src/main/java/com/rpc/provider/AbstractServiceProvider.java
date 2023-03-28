package com.rpc.provider;

import com.rpc.properties.RPCProperties;
import com.rpc.register.ServerRegister;
import enums.ErrorEnum;
import exception.RPCException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class AbstractServiceProvider implements ServiceProvider {

    protected final Map<String, Object> registeredServices = new ConcurrentHashMap<>();
    private final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    protected ServerRegister serverRegister;

    @Override
    public <T> void addService(String serviceName, T serviceBean, InetSocketAddress serviceAddress) {
        if (registeredServices.containsKey(serviceName)) {
            return;
        }
        if (atomicBoolean.compareAndSet(false, true)) {
            serverRegister.registerService(RPCProperties.applicationName, serviceAddress);
        }
        registeredServices.put(serviceName, serviceBean);
        log.info("publish service {} => {}", serviceName, serviceAddress);
    }

    @Override
    public Object getService(String serviceName) {
        Object o = registeredServices.get(serviceName);
        if (o == null) {
            throw new RPCException(ErrorEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return o;
    }

    @Override
    public void clearService(InetSocketAddress serviceAddress) {
        atomicBoolean.compareAndSet(true, false);
        serverRegister.clearServer(serviceAddress);
    }
}
