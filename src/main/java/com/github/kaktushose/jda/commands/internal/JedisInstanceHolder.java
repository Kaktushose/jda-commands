package com.github.kaktushose.jda.commands.internal;

import com.github.kaktushose.jda.commands.entities.JDACommands;
import com.github.kaktushose.jda.commands.entities.JDACommandsBuilder;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisInstanceHolder {
    static JedisPool pool;
    public static synchronized JedisPool getInstance() {
        if (pool == null) {
            pool = new JedisPool(new JedisPoolConfig(), JDACommands.getInstance().getSettings().getRedisHost(), JDACommands.getInstance().getSettings().getRedisPort());
        }
        return pool;
    }
}
