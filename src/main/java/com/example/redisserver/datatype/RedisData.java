package com.example.redisserver.datatype;

/**
 * @author WesleyGo
 */
public interface RedisData
{
    long timeout();

    void setTimeout(long timeout);
}
