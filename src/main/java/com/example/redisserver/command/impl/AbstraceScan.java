package com.example.redisserver.command.impl;


import com.example.redisserver.RedisCore;
import com.example.redisserver.command.Command;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.RespArray;
import io.netty.channel.ChannelHandlerContext;

public abstract class AbstraceScan implements Command
{

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        Resp[]     array       = new Resp[2];
        BulkString blukStrings = new BulkString(new BytesWrapper("0".getBytes(CHARSET)));
        array[0] = blukStrings;
        array[1] = get(redisCore);
        ctx.writeAndFlush(new RespArray(array));
    }

    protected abstract RespArray get(RedisCore redisCore);
}
