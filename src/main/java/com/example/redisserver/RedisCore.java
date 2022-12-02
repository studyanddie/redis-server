package com.example.redisserver;


import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.datatype.RedisData;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Set;

public interface RedisCore
{
    Set<BytesWrapper> keys();

    void putClient(BytesWrapper connectionName, Channel channelContext);

    boolean exist(BytesWrapper key);

    void put(BytesWrapper key, RedisData redisData);

    RedisData get(BytesWrapper key);

    long remove(List<BytesWrapper> keys);

    void cleanAll();
}