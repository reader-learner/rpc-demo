package com.rpc.transport.netty.server;

import com.rpc.hook.ServerShutdownHook;
import com.rpc.properties.RPCProperties;
import com.rpc.provider.ServiceProvider;
import com.rpc.transport.AbstractRPCServer;
import extension.ExtensionLoader;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import utils.RuntimeUtil;
import utils.thread.ThreadPoolFactoryUtil;

import java.net.InetSocketAddress;

@Slf4j
@Component("com.rpc.transport.netty.server.NettyRPCServer")
public class NettyRPCServer extends AbstractRPCServer {

    public NettyRPCServer() {
        serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class)
                .getExtension(RPCProperties.getRPCProperties().getProvider());
    }

    @Override
    public void start() {
        //清除服务
        ServerShutdownHook.getServerShutdownHook().clearAllServiceOnClose(new InetSocketAddress(host, port), serviceProvider);
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);

        ChannelFuture channelFuture = null;
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    // 默认开启 Nagle 算法
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerInitializer());
            channelFuture = bootstrap.bind(host, port);
            log.debug("netty server started on port {}", port);
            // 注册标注了 @RpcService 的服务
            registryServices();
            channelFuture.channel().closeFuture().sync();
            //清除服务
            ServerShutdownHook.getServerShutdownHook().clearAllServiceOnClose(new InetSocketAddress(host, port), serviceProvider);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            eventLoopGroup.shutdownGracefully();
            if (channelFuture != null) {
                channelFuture.channel().closeFuture();
            }
            log.info("shutdown bossGroup and workerGroup and channelFuture");
        }
    }

    /**
     * 注册@RPCService服务
     */
    private void registryServices() {
        if (!CollectionUtils.isEmpty(serviceConfigs)) {
            serviceConfigs.forEach(this::publishService);
        } else {
            log.warn("@RpcService set is empty");
        }
    }
}
