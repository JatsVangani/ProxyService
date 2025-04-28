package com.practo.proxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "services")
public class ServiceConfig {
    private Map<String, String> hosts;
    private Map<String, String> keys;

    /**
     * Gets the map of service hosts.
     *
     * @return the map of service hosts
     */
    public Map<String, String> getHosts() {
        return hosts;
    }

    /**
     * Sets the map of service hosts.
     *
     * @param hosts the map of service hosts to set
     */
    public void setHosts(Map<String, String> hosts) {
        this.hosts = hosts;
    }

    /**
     * Gets the map of service keys.
     *
     * @return the map of service keys
     */
    public Map<String, String> getKeys() {
        return keys;
    }

    /**
     * Sets the map of service keys.
     *
     * @param keys the map of service keys to set
     */
    public void setKeys(Map<String, String> keys) {
        this.keys = keys;
    }
} 