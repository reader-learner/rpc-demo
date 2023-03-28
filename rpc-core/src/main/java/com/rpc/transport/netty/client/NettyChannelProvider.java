package com.rpc.transport.netty.client;

import enums.ErrorEnum;
import exception.RPCException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * map管理链接netty的channel 通过InetSocketAddress(IP address + port number)   查找对应的channel
 */
@Slf4j
public class NettyChannelProvider {
    /**
     * 用于连接服务器（构造 Netty 客户端时传入）
     */
    private final Bootstrap bootStrap;

    private final Map<String, Channel> channelMap;

    public NettyChannelProvider(Bootstrap bootStrap) {
        this.bootStrap = bootStrap;
        this.channelMap = new ConcurrentHashMap<>();
    }

    public Channel getChannel(InetSocketAddress serviceAddress) {
        String address = serviceAddress.toString();
        Channel channel = channelMap.get(address);
        if (null != channel) {
            if (channel.isActive()) {
                return channel;
            } else {
                channelMap.remove(address);
            }
        }
        channel = doConnect(serviceAddress);
        channelMap.put(address, channel);
        return channel;
    }
    @SneakyThrows
    private Channel doConnect(InetSocketAddress serviceAddress) {
        CompletableFuture<Channel> future=new CompletableFuture<>();
        bootStrap.connect(serviceAddress).addListener((ChannelFutureListener) listener->{
            if (listener.isSuccess()) {
                log.info("The client has connected [{}] successful!", serviceAddress.toString());
                future.complete(listener.channel());
            } else {
                log.error("failed to connect to server: [{}]", serviceAddress);
                throw new RPCException(ErrorEnum.FAILED_TO_CONNECT_TO_SERVER);
            }
        });
        return  future.get();
    }

}
