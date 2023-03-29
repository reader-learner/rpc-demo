package com.rpc.transport.netty.client;

import com.rpc.transport.netty.codec.NettyMessageDecoder;
import com.rpc.transport.netty.codec.NettyMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    private final Bootstrap bootStrap;

    public ClientInitializer(Bootstrap bootstrap) {
        this.bootStrap = bootstrap;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new IdleStateHandler(5, 5, 5, TimeUnit.SECONDS));
        p.addLast(new NettyMessageEncoder());
        p.addLast(new NettyMessageDecoder());
        p.addLast(new NettyClientHandler(new NettyChannelProvider(bootStrap)));
        p.addLast(new ReconnectHandler());
    }
}

