package com.rpc.transport.netty.client;

import com.rpc.transport.constant.RPCProtocolConstant;
import com.rpc.transport.dto.RPCMessage;
import com.rpc.transport.dto.RPCResponse;
import com.rpc.transport.netty.IdleHandler;
import factory.SingletonFactory;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Customize the client ChannelHandler to process the data sent by the server
 *
 * <p>
 * 如果继承自 SimpleChannelInboundHandler 的话就不要考虑 ByteBuf 的释放 ，{@link SimpleChannelInboundHandler} 内部的
 * channelRead 方法会替你释放 ByteBuf ，避免可能导致的内存泄露问题。详见《Netty进阶之路 跟着案例学 Netty》0
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private final UnprocessedRequests unprocessedRequests;
    private final NettyChannelProvider channelProvider;
    private volatile AtomicInteger idleTimes = new AtomicInteger(0);

    public NettyClientHandler(NettyChannelProvider channelProvider) {
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = channelProvider;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RPCMessage) {
                RPCMessage message = (RPCMessage) msg;
                byte messageType = ((RPCMessage) msg).getMessageType();
                if (messageType == RPCProtocolConstant.RESPONSE_TYPE) {
                    unprocessedRequests.complete((RPCResponse<Object>) message.getPayload());
                    return;
                }

                RPCMessage rpcResponse = IdleHandler.invoke(message, messageType);
                if (rpcResponse != null) {
                    ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                }

            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    //在出现超时事件时会被触发，包括读空闲超时或者写空闲超时；
    // 客户端写 服务端读，客户端无法感知服务端是否下线
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state.equals(IdleState.WRITER_IDLE)) {
                // 写超时，向服务端发送心跳
                log.info("client write idle happen [{}]", ctx.channel().remoteAddress());
                Channel channel = channelProvider.getChannel((InetSocketAddress) ctx.channel().remoteAddress());
                channel.writeAndFlush(IdleHandler.sendIdleRequest())
                        .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else if (state.equals(IdleState.READER_IDLE)) {
                // 读超时，服务端未响应心跳
                int i = idleTimes.incrementAndGet();
                log.error("server do not send idle response times{}" + i);
                if (i >= 15) {
                    log.error("server disconnection");
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client catch exception：", cause);
        ctx.close();
    }
}
