package com.example.redisserver.command.impl.set;


import com.example.redisserver.RedisCore;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.command.WriteCommand;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.datatype.RedisData;
import com.example.redisserver.datatype.RedisSet;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.RespInt;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sadd implements WriteCommand
{
    List<BytesWrapper> member;
    private BytesWrapper key;

    @Override
    public CommandType type()
    {
        return CommandType.sadd;
    }

    @Override
    public void setContent(Resp[] array)
    {
        key = ((BulkString) array[1]).getContent();
        member = Stream.of(array).skip(2).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        RedisData redisData = redisCore.get(key);
        if (redisData == null)
        {
            RedisSet redisSet = new RedisSet();
            int      sadd     = redisSet.sadd(member);
            redisCore.put(key, redisSet);
            ctx.writeAndFlush(new RespInt(sadd));
        }
        else if (redisData instanceof RedisSet)
        {
            RedisSet redisSet = (RedisSet) redisData;
            int      sadd     = redisSet.sadd(member);
            ctx.writeAndFlush(new RespInt(sadd));
        }
        else
        {
            throw new IllegalArgumentException("类型不匹配");
        }
    }

    @Override
    public void handle(RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null)
        {
            RedisSet redisSet = new RedisSet();
            redisSet.sadd(member);
            redisCore.put(key, redisSet);
        }
        else if (redisData instanceof RedisSet)
        {
            RedisSet redisSet = (RedisSet) redisData;
            redisSet.sadd(member);
        }
        else
        {
            throw new IllegalArgumentException("类型不匹配");
        }
    }
}
