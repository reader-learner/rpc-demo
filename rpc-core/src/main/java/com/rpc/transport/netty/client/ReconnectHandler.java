package com.rpc.transport.netty.client;

import factory.SingletonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReconnectHandler extends ChannelInboundHandlerAdapter {
    private static final int MAX_RECONNECT_TIMES = 5; // 最大重连次数
    private static final int RECONNECT_DELAY = 5000; // 重连延迟时间
    private final NettyChannelProvider channelProvider = SingletonFactory.getInstance(NettyChannelProvider.class);
    private final Bootstrap bootstrap = SingletonFactory.getInstance(NettyChannelProvider.class)
            .getBootStrap();
    private int reconnectCount = 0;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (reconnectCount < MAX_RECONNECT_TIMES) {
            log.error("连接断开，尝试进行第 " + (reconnectCount + 1) + " 次重连...");
            Thread.sleep(RECONNECT_DELAY); // 等待一定时间后进行重连
            bootstrap.remoteAddress(ctx.channel().remoteAddress()).connect();
            reconnectCount++;
        } else {
            log.error("已达到最大重连次数，无法重连！");
            ctx.close();
        }
    }
}
