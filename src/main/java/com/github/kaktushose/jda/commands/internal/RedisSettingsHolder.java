package com.github.kaktushose.jda.commands.internal;

import javax.annotation.Nonnull;

public class RedisSettingsHolder {

    private boolean isRedisEnabled;
    private String redisHost;
    private int redisDatabase;
    private Integer redisPort;

    public RedisSettingsHolder(boolean isRedisEnabled, String redisHost, Integer redisPort, int redisDatabase) {
        this.isRedisEnabled = isRedisEnabled;
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        this.redisDatabase = redisDatabase;
    }

    /**
     * This gets the boolean, which specifies if jedis should or shouldn't be enabled
     *
     * @return bool if redis should be enabled or shouldn't
     */

    public boolean isRedisEnabled() {
        return isRedisEnabled;
    }

    /**
     * This gets the hostname/ipv4 which jedis is supposed to connect to
     *
     * @return hostname / IPv4 address of the redis server
     */
    public String getRedisHost() {
        return redisHost;
    }

    /**
     * This gets the port which jedis is supposed to connect to
     *
     * @return port to connect to on the redis server
     */
    public Integer getRedisPort() {
        return redisPort;
    }

    /**
     * This gets the database, which jedis should save the prefixes in
     *
     * @return database number, on which redis is supposed to save the prefixes on
     */
    public int getRedisDatabase() {
        return redisDatabase;
    }

}

