package com.example.redisserver.command.impl.zset;


import com.example.redisserver.RedisCore;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.command.WriteCommand;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.datatype.RedisData;
import com.example.redisserver.datatype.RedisZset;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.RespInt;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public class Zadd implements WriteCommand
{
    private BytesWrapper key;
    private List<RedisZset.ZsetKey> keys;

    @Override
    public CommandType type()
    {
        return CommandType.zadd;
    }

    @Override
    public void setContent(Resp[] array)
    {
        key = ((BulkString) array[1]).getContent();
        keys = new ArrayList<>();
        for (int i = 2; i + 1 < array.length; i += 2)
        {
            long         score  = Long.parseLong(((BulkString) array[i]).getContent().toUtf8String());
            BytesWrapper member = ((BulkString) array[i + 1]).getContent();
            keys.add(new RedisZset.ZsetKey(member, score));
        }
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        //获取键值对的键key
        RedisData redisData = redisCore.get(key);
        if (redisData == null)
        {
            RedisZset redisZset = new RedisZset();
            int       add       = redisZset.add(keys);
            redisCore.put(key, redisZset);
            ctx.writeAndFlush(new RespInt(add));
        }
        else if (redisData instanceof RedisZset)
        {
            RedisZset redisZset = (RedisZset) redisData;
            int       add       = redisZset.add(keys);
            ctx.writeAndFlush(new RespInt(add));
        }
        else
        {
            throw new UnsupportedOperationException("类型不匹配");
        }
    }

    @Override
    public void handle(RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null)
        {
            RedisZset redisZset = new RedisZset();
            int       add       = redisZset.add(keys);
            redisCore.put(key, redisZset);
        }
        else if (redisData instanceof RedisZset)
        {
            RedisZset redisZset = (RedisZset) redisData;
            int       add       = redisZset.add(keys);
        }
        else
        {
            throw new UnsupportedOperationException("类型不匹配");
        }
    }
}
