package com.rpc.transport.netty.server;

import com.rpc.annotation.RPCService;
import com.rpc.config.ServiceConfig;
import com.rpc.hook.ServerShutdownHook;
import com.rpc.properties.RPCProperties;
import com.rpc.provider.ServiceProvider;
import com.rpc.transport.AbstractRPCServer;
import com.rpc.transport.netty.codec.NettyMessageDecoder;
import com.rpc.transport.netty.codec.NettyMessageEncoder;
import extension.ExtensionLoader;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NettyRPCServer extends AbstractRPCServer implements BeanPostProcessor {

    {
        serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class)
                .getExtension(RPCProperties.provider);
    }

    public NettyRPCServer() {
    }

    /**
     * 扫描标注了 @RpcService 的服务
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RPCService rpcService = bean.getClass().getAnnotation(RPCService.class);
        if (null != rpcService) {
            ServiceConfig serviceConfig = ServiceConfig.builder().service(rpcService).group(rpcService.group()).serverName(RPCProperties.applicationName)
                    .version(rpcService.version()).build();
            serviceConfigs.add(serviceConfig);
            log.info(serviceConfig.getServerName() + "find rpc service: [{}]", serviceConfig.getServiceName());
        }
        return bean;
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
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 开启 Netty 心跳机制
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new NettyMessageDecoder());
                            pipeline.addLast(new NettyMessageEncoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
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
