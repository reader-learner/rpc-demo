package com.rpc.transport.netty.server;

import com.rpc.properties.RPCProperties;
import com.rpc.provider.ServiceProvider;
import com.rpc.transport.netty.IdleHandler;
import com.rpc.transport.netty.work.ThreadPoolExecutorFactory;
import com.rpc.transport.netty.work.WorkHandler;
import extension.ExtensionLoader;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final ServiceProvider serviceProvider;
    private final ThreadPoolExecutorFactory executorFactory = new ThreadPoolExecutorFactory();

    public NettyServerHandler() {
        serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class)
                .getExtension(RPCProperties.getRPCProperties().getProvider());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        executorFactory.getThreadPool().execute(new WorkHandler(msg, ctx,serviceProvider));
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state.equals(IdleState.WRITER_IDLE)) {
                // 写超时，向客户端发送心跳消息
                log.info("server write idle happen [{}]", ctx.channel().remoteAddress());
                ctx.writeAndFlush(IdleHandler.sendIdleRequest())
                        .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else if (state.equals(IdleState.READER_IDLE)) {
                // 读超时，客户端未响应心跳消息，断开连接
                ctx.close();
                log.error("client do not send idle response");

            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server catch exception：", cause);
        ctx.close();
    }
}
