package com.example.redisserver.command.impl;



import com.example.redisserver.RedisCore;
import com.example.redisserver.command.Command;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.datatype.*;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.SimpleString;
import io.netty.channel.ChannelHandlerContext;

public class Type implements Command
{
    private BytesWrapper key;

    @Override
    public CommandType type()
    {
        return CommandType.type;
    }

    @Override
    public void setContent(Resp[] array)
    {
        key = ((BulkString) array[1]).getContent();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        RedisData redisData = redisCore.get(key);
        if (redisData == null)
        {
            ctx.writeAndFlush(new SimpleString("none"));
        }
        else if (redisData instanceof RedisString)
        {
            ctx.writeAndFlush(new SimpleString("string"));
        }
        else if (redisData instanceof RedisList)
        {
            ctx.writeAndFlush(new SimpleString("list"));
        }
        else if (redisData instanceof RedisSet)
        {
            ctx.writeAndFlush(new SimpleString("set"));
        }
        else if (redisData instanceof RedisHash)
        {
            ctx.writeAndFlush(new SimpleString("hash"));
        }
        else if (redisData instanceof RedisZset)
        {
            ctx.writeAndFlush(new SimpleString("zset"));
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
}
