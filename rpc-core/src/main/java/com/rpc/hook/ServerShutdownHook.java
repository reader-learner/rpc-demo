package com.rpc.hook;

import lombok.extern.slf4j.Slf4j;
import com.rpc.provider.ServiceProvider;

import java.net.InetSocketAddress;

/**
 * 服务器关闭前，清理已经注册的服务
 */
@Slf4j
public class ServerShutdownHook {
    private static final ServerShutdownHook serverShutdownHook = new ServerShutdownHook();

    private ServerShutdownHook() {
    }

    public static ServerShutdownHook getServerShutdownHook() {
        return serverShutdownHook;
    }


    public void clearAllServiceOnClose(InetSocketAddress address, ServiceProvider serviceProvider) {
        log.info("add shutdownHook to clearService");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            serviceProvider.clearService(address);
        }));
    }
}
