package com.example.redisserver;

public interface RedisServer
{
    void start();

    void close();

    RedisCore getRedisCore();
}
