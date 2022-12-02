package com.example.redisserver.command.impl.set;


import com.example.redisserver.RedisCore;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.command.impl.AbstraceScan;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.datatype.RedisSet;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.RespArray;

import java.util.List;
import java.util.stream.Collectors;

public class Sscan extends AbstraceScan
{
    private BytesWrapper key;

    @Override
    public CommandType type()
    {
        return CommandType.sscan;
    }

    @Override
    public void setContent(Resp[] array)
    {
        key = ((BulkString) array[1]).getContent();
    }

    @Override
    protected RespArray get(RedisCore redisCore)
    {
        RedisSet redisSet = (RedisSet) redisCore.get(key);
        List<BulkString> collect  = redisSet.keys().stream().map(keyName -> new BulkString(keyName)).collect(Collectors.toList());
        return new RespArray(collect.toArray(new Resp[collect.size()]));
    }
}
