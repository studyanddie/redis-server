package com.example.redisserver.command.impl;

import com.example.redisserver.RedisCore;
import com.example.redisserver.command.Command;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.SimpleString;
import io.netty.channel.ChannelHandlerContext;

public class Ping implements Command
{

    @Override
    public CommandType type()
    {
        return CommandType.lrem;
    }

    @Override
    public void setContent(Resp[] array)
    {
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        ctx.write(new SimpleString("PONG"));
        ctx.flush();
    }
}
