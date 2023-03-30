package com.rpc.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import utils.PropertiesUtil;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "rpc")
@PropertySource(value = "classpath:rpc.properties")
@Configuration
@Getter
@Setter
@Slf4j
public class RPCProperties {

    private static volatile RPCProperties rpcProperties;
    private String host = InetAddress.getLoopbackAddress().getHostAddress();
    private String applicationName = "server";
    private int port = 8989;
    private String loadBalancer = "RoundRobinLoadBalancer";
    private String discovery = "NacosServerDiscovery";
    private String register = "NacosServerRegister";
    private String serialize = "JsonSerializer";
    private String rpcClient = "NettyRPCClient";
    private String proxy = "JdkDynamicProxy";
    private String provider = "NacosServiceProvider";
    private String nacosAddress = "192.168.88.128:8848";
    private String discoverCache = "LocalServerDiscoveryCache";
    private String compress = "GzipCompress";

    public static RPCProperties getRPCProperties() {
        if (rpcProperties == null) {
            synchronized (RPCProperties.class) {
                if (rpcProperties == null) {
                    genRPCProperties();
                }
            }
        }
        return rpcProperties;
    }

    private static void genRPCProperties() {
        rpcProperties = new RPCProperties();
        Properties properties = PropertiesUtil.readProperties("rpc.properties");
        if (properties == null) return;
        try {
            Map<String, Field> fieldMap = Arrays.stream(RPCProperties.class.getDeclaredFields())
                    .collect(Collectors.toMap(
                            field -> field.getName().toLowerCase(Locale.ROOT), field -> field));

            for (String propertiesName : properties.stringPropertyNames()) {
                String propertyValue = properties.getProperty(propertiesName);
                propertiesName = propertiesName.substring(propertiesName.lastIndexOf('.') + 1).replace("-", "");
                Field field = fieldMap.get(propertiesName);
                if (field == null) continue;
                Class<?> fieldType = field.getType();
                Object value;
                if (fieldType == String.class) {
                    value = propertyValue;
                } else {
                    value = Integer.parseInt(propertyValue);
                }
                field.setAccessible(true);
                if (value != null) {
                    field.set(rpcProperties, value);
                }
            }

        } catch (IllegalAccessException e) {
            log.error("get properties value error");
        }
    }


}
