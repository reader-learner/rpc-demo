package com.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceConfig {
    private Object service;
    private String group;
    private String version;
    private String serverName;

    public String getInterfaceName() {
        return service.getClass().getInterfaces()[0].getName();
    }

    public String getServiceName() {

        return this.getGroup() + ' ' + this.getVersion() + ' ' + getInterfaceName();
    }
}
