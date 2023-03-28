package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配置类 nocos等配置信息
 */
@AllArgsConstructor
@Getter
public enum ConfigEnum {
    RPC_CONFIG_PATH("config.properties"),
    ZK_ADDRESS("rpc.zookeeper.address"),
    ZK_REGISTERED_ROOT_PATH("/registered"),
    NACOS_ADDRESS("rpc.nacos.address"),
    CONFIG_PREFIX("Customization.rpc");
    private final String value;
}
