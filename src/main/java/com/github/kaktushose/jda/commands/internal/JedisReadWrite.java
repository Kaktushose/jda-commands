package com.github.kaktushose.jda.commands.internal;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisReadWrite {
    public static String getString(int db, String key){
        JedisPool pool = JedisInstanceHolder.getInstance();
        try (Jedis jedis = pool.getResource()) {
            jedis.select(db);
            return jedis.get(key);
        }
    }
    public static void insertString(int db, String key, String value){
        JedisPool pool = JedisInstanceHolder.getInstance();
        try (Jedis jedis = pool.getResource()) {
            jedis.select(db);
            jedis.set(key, value);
        }
    }
    public static void delString(int db, String key){
        JedisPool pool = JedisInstanceHolder.getInstance();
        try (Jedis jedis = pool.getResource()) {
            jedis.select(db);
            jedis.del(key);
        }
    }
}