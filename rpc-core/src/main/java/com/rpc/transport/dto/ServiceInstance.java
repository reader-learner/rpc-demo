package com.rpc.transport.dto;

import lombok.Builder;
import lombok.Data;

import java.net.URI;
import java.util.Map;

@Data
@Builder
public class ServiceInstance {
    private String serverName;

    private String host; // 主机名或 IP 地址

    private int port; // 端口号

    private Map<String, String> metadata; // 其他元数据

}

