package com.hzy.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ai.redis")
public class ConfigProperty {
    private String host;
    private int port;
    private int timeout;
    private String password;

    public ConfigProperty() {
    }

    public ConfigProperty(String host, Integer port, Integer timeout, String password) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.password = password;
    }

    /**
     * 获取
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取
     * @return port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * 设置
     * @param port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 获取
     * @return timeout
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * 设置
     * @param timeout
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * 获取
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }



    public String toString() {
        return "ConfigProperty{host = " + host + ", port = " + port + ", timeout = " + timeout + ", password = " + password + "}";
    }
}
