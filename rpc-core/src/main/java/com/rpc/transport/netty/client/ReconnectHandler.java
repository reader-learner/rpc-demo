package com.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReconnectHandler extends ChannelInboundHandlerAdapter {
    private static final int MAX_RECONNECT_TIMES = 5; // 最大重连次数
    private static final int RECONNECT_DELAY = 5000; // 重连延迟时间
    private int reconnectCount = 0;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (reconnectCount < MAX_RECONNECT_TIMES) {
            log.error("连接断开，尝试进行第 " + (reconnectCount + 1) + " 次重连...");
            Thread.sleep(RECONNECT_DELAY); // 等待一定时间后进行重连
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(ctx.channel().eventLoop())
                    .channel(NioSocketChannel.class)
                    .remoteAddress(ctx.channel().remoteAddress())
                    .handler(new ClientInitializer(bootstrap));
            bootstrap.connect();
            reconnectCount++;
        } else {
            log.error("已达到最大重连次数，无法重连！");
            ctx.close();
        }
    }
}
