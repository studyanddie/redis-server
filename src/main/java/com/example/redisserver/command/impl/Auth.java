package com.example.redisserver.command.impl;


import com.example.redisserver.RedisCore;
import com.example.redisserver.command.Command;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.SimpleString;
import io.netty.channel.ChannelHandlerContext;

public class Auth implements Command
{
    private String password;

    @Override
    public CommandType type()
    {
        return CommandType.auth;
    }

    @Override
    public void setContent(Resp[] array)
    {
        BulkString blukStrings = (BulkString) array[1];
        byte[]     content     = blukStrings.getContent().getByteArray();
        if (content.length == 0)
        {
            password = "";
        }
        else
        {
            password = new String(content);
        }
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        SimpleString ok = new SimpleString("OK");
        ctx.writeAndFlush(ok);
    }
}
