package com.rpc.utils;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.rpc.transport.dto.ServiceInstance;
import enums.ErrorEnum;
import exception.RPCException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class NacosUtils {
    private static final Set<String> registerServers = ConcurrentHashMap.newKeySet();
    private static final NamingService namingService;
    private static final String DEFAULT_NACOS_ADDRESS = "192.168.88.128:8848";

    static {
        namingService = getNamingService();
    }


    public static NamingService getNamingService() {
        try {
            return NamingFactory.createNamingService(DEFAULT_NACOS_ADDRESS);
        } catch (NacosException e) {
            log.error("connect to nacos [{}] fail", DEFAULT_NACOS_ADDRESS);
            throw new RPCException(ErrorEnum.FAILED_TO_CONNECT_TO_REGISTRY);
        }
    }

    /**
     * @param serverName 服务名称
     * @param address    地址
     */
    public static void registerServer(String serverName, InetSocketAddress address) {
        try {
            namingService.registerInstance(serverName, address.getHostName(), address.getPort());
            log.info("register instance " + serverName + " " + address.getHostName() + " " + address.getPort());
            registerServers.add(serverName);
        } catch (NacosException e) {
            log.error("register Server " + serverName + " fail");
            throw new RPCException(ErrorEnum.REGISTER_SERVICE_FAILED);
        }

    }

    /**
     * 根据服务地址清理 nacos
     *
     * @param address 地址
     */
    public static void deregisterInstance(InetSocketAddress address) {
        if (registerServers.isEmpty()) return;
        for (String registerServer : registerServers) {
            try {
                namingService.deregisterInstance(registerServer, address.getHostName(), address.getPort());
            } catch (NacosException e) {
                log.error("clear registry for service [{}] fail", registerServer, e);
            }
        }
        log.info("All registered services on the server are cleared: [{}]", registerServers);
    }

    /**
     * @param serverName 服务名称
     * @return 地址实例
     */
//    public static List<String> getAllInstance(String serverName) {
//        List<String> rs = new ArrayList<>();
//        if (StringUtils.isEmpty(serverName)) return rs;
//        try {
//            List<Instance> allInstances = namingService.getAllInstances(serverName);
//            allInstances.forEach(v -> {
//                rs.add(v.getIp() + ":" + v.getPort());
//            });
//        } catch (NacosException e) {
//            log.error("exception when get " + serverName + " instances ", e);
//        }
//        return rs;
//    }

    public static List<ServiceInstance> getServerAllInstances(String serverName) {
        List<ServiceInstance> rs = new ArrayList<>();
        if (StringUtils.isEmpty(serverName)) return rs;
        try {
            List<Instance> allInstances = namingService.getAllInstances(serverName);
            for (Instance instance : allInstances) {
                ServiceInstance serviceInstance = ServiceInstance.builder().serverName(serverName)
                        .host(instance.getIp()).port(instance.getPort()).metadata(instance.getMetadata()).build();
                rs.add(serviceInstance);
            }
        } catch (NacosException e) {
            log.error("exception when get " + serverName + " instances ", e);
        }
        return rs;
    }


}
