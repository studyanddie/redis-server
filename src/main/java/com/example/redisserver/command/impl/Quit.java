package com.example.redisserver.command.impl;



import com.example.redisserver.RedisCore;
import com.example.redisserver.command.Command;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.SimpleString;
import io.netty.channel.ChannelHandlerContext;

public class Quit implements Command
{
    @Override
    public CommandType type()
    {
        return CommandType.quit;
    }

    @Override
    public void setContent(Resp[] array)
    {
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        ctx.writeAndFlush(SimpleString.OK);
        ctx.close();
    }
}
