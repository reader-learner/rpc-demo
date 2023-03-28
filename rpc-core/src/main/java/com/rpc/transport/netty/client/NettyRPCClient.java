package com.rpc.transport.netty.client;

import com.rpc.transport.constant.RPCProtocolConstant;
import com.rpc.transport.dto.RPCMessage;
import com.rpc.transport.dto.RPCRequest;
import com.rpc.transport.dto.RPCResponse;
import com.rpc.transport.netty.codec.NettyMessageDecoder;
import com.rpc.transport.netty.codec.NettyMessageEncoder;
import enums.CompressTypeEnum;
import enums.SerializationEnum;
import factory.SingletonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import com.rpc.transport.AbstractRPCClient;

import java.net.InetSocketAddress;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 实现RPCClient接口
 */
@Slf4j
public class NettyRPCClient extends AbstractRPCClient {
    private final Bootstrap bootStrap;
    private final EventLoopGroup eventLoopGroup;
    /**
     * 管理 Netty 的 channel
     */
    private final NettyChannelProvider channelProvider;
    /**
     * 未处理的请求（还未被服务器响应）
     */
    private final UnprocessedRequests unprocessedRequests;


    public NettyRPCClient() {
        bootStrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        channelProvider = SingletonFactory.getInstance(NettyChannelProvider.class, bootStrap);
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        bootStrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(0, 20, 0, TimeUnit.SECONDS));
                        p.addLast(new NettyMessageEncoder());
                        p.addLast(new NettyMessageDecoder());
                        p.addLast(new NettyClientHandler(channelProvider));
                    }
                });
    }


    @Override
    public RPCResponse<Object> sendRequest(RPCRequest request) {
        return sendRequest(request, SerializationEnum.JSON, CompressTypeEnum.GZIP);
    }

    @Override
    public RPCResponse<Object> sendRequest(RPCRequest request, SerializationEnum serializationEnum) {
        return sendRequest(request, serializationEnum, CompressTypeEnum.GZIP);
    }

    @Override
    public RPCResponse<Object> sendRequest(RPCRequest request, CompressTypeEnum compressTypeEnum) {
        return sendRequest(request, SerializationEnum.JSON, compressTypeEnum);
    }

    @SneakyThrows
    @Override
    public RPCResponse<Object> sendRequest(RPCRequest request, SerializationEnum serializationEnum, CompressTypeEnum compressTypeEnum) {
        checkEnum(serializationEnum, compressTypeEnum);
        CompletableFuture<RPCResponse<Object>> completableFuture = new CompletableFuture<>();
        //服务发现获取地址
        InetSocketAddress serverAddress = serverDiscovery.lookupService(request);
        Channel channel = channelProvider.getChannel(serverAddress);
        if (channel != null && channel.isActive()) {
            //存储请求
            unprocessedRequests.put(request.getRequestId(), completableFuture);
            //构造message
            RPCMessage message = RPCMessage.builder().messageType(RPCProtocolConstant.REQUEST_TYPE)
                    .requestId(requestIdProvider.incrementAndGet()).payload(request)
                    .compress(compressTypeEnum.getCode()).codec(serializationEnum.getCode()).build();
            channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", message);
                } else {
                    future.channel().close();
                    completableFuture.completeExceptionally(future.cause());
                    log.error("send failed: ", future.cause());
                }
            });
        } else throw new IllegalStateException();
        return completableFuture.get();
    }

    public void checkEnum(SerializationEnum serializationEnum, CompressTypeEnum compressTypeEnum) {
        if (!EnumSet.allOf(SerializationEnum.class).contains(serializationEnum)
                || !EnumSet.allOf(CompressTypeEnum.class).contains(compressTypeEnum)) {
            throw new IllegalArgumentException();
        }

    }


}
