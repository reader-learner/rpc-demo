package com.rpc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Data
@EnableConfigurationProperties(RPCProperties.class)
@ConfigurationProperties(prefix = "rpc")
@Component
public class RPCProperties {
    public static String host = InetAddress.getLoopbackAddress().getHostAddress();
    public static String applicationName = "server";
    public static int port = 8002;
    public static String loadBalancer = "RoundRobinLoadBalancer";
    public static String discovery = "NacosServerDiscovery";
    public static String register = "NacosServerRegister";
    public static String serialize = "JsonSerializer";
    public static String rpcClient = "NettyRPCClient";
    public static String proxy = "JdkDynamicProxy";
    public static String provider = "NacosServiceProvider";
}
