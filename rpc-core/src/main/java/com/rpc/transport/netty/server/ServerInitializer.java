package com.rpc.transport.netty.server;

import com.rpc.transport.netty.codec.NettyMessageDecoder;
import com.rpc.transport.netty.codec.NettyMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 开启 Netty 心跳机制
        pipeline.addLast(new IdleStateHandler(30, 10, 10, TimeUnit.SECONDS));
        pipeline.addLast(new NettyMessageDecoder());
        pipeline.addLast(new NettyMessageEncoder());
        pipeline.addLast(new NettyServerHandler());
    }
}

