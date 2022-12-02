package com.example.redisserver.command.impl;


import com.example.redisserver.RedisCore;
import com.example.redisserver.command.Command;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.RespInt;
import io.netty.channel.ChannelHandlerContext;

public class Exists implements Command
{
    private BytesWrapper key;

    @Override
    public CommandType type()
    {
        return CommandType.exists;
    }

    @Override
    public void setContent(Resp[] array)
    {
        key = ((BulkString) array[1]).getContent();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        boolean exist = redisCore.exist(key);
        if (exist)
        {
            ctx.writeAndFlush(new RespInt(1));
        }
        else
        {
            ctx.writeAndFlush(new RespInt(0));
        }
    }
}
