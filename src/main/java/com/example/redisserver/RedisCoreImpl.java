package com.example.redisserver;


import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.datatype.RedisData;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author WesleyGo
 * 底层主结构
 */
public class RedisCoreImpl implements RedisCore
{
    /**
     *    客户端可能使用hash路由，更换为跳表更好的避免hash冲突
     */
    //用来存key-value
    private final ConcurrentNavigableMap<BytesWrapper, RedisData> map         = new ConcurrentSkipListMap<BytesWrapper, RedisData>();
//    private final ConcurrentHashMap<BytesWrapper, RedisData> map         = new ConcurrentHashMap<BytesWrapper, RedisData>();

    private final ConcurrentHashMap<BytesWrapper, Channel> clients     = new ConcurrentHashMap<>();
    private final Map<Channel, BytesWrapper>               clientNames = new ConcurrentHashMap<>();

    @Override
    public Set<BytesWrapper> keys()
    {
        return map.keySet();
    }

    @Override
    public void putClient(BytesWrapper connectionName, Channel channelContext)
    {
        clients.put(connectionName, channelContext);
        clientNames.put(channelContext, connectionName);
    }

    //判断是否存在
    @Override
    public boolean exist(BytesWrapper key)
    {
        return map.containsKey(key);
    }
    //存数据
    @Override
    public void put(BytesWrapper key, RedisData redisData)
    {
        map.put(key, redisData);
    }

    //取数据
    @Override
    public RedisData get(BytesWrapper key)
    {
        RedisData redisData = map.get(key);
        if (redisData == null)
        {
            return null;
        }
        if (redisData.timeout() == -1)
        {
            return redisData;
        }
        if (redisData.timeout() < System.currentTimeMillis())
        {
            map.remove(key);
            return null;
        }
        return redisData;
    }

    @Override
    public long remove(List<BytesWrapper> keys)
    {
        return keys.stream().peek(map::remove).count();
    }

    @Override
    public void cleanAll()
    {
        map.clear();
    }
}