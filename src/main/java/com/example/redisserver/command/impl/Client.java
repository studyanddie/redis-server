package com.example.redisserver.command.impl;

import com.example.redisserver.RedisCore;
import com.example.redisserver.command.Command;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.SimpleString;
import com.example.redisserver.util.TRACEID;
import io.netty.channel.ChannelHandlerContext;

public class Client implements Command
{
    private String subCommand;
    private Resp[] array;

    @Override
    public CommandType type()
    {
        return CommandType.client;
    }

    @Override
    public void setContent(Resp[] array)
    {
        this.array = array;
        subCommand = ((BulkString) array[1]).getContent().toUtf8String();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        String traceId = TRACEID.currentTraceId();
        LOGGER.debug("traceId:{} 当前的子命令是：{}"+traceId+subCommand);
        switch (subCommand)
        {
            case "setname":
                BytesWrapper connectionName = ((BulkString) array[2]).getContent();
                redisCore.putClient(connectionName, ctx.channel());
                break;
            default:
                throw new IllegalArgumentException();
        }
        ctx.writeAndFlush(new SimpleString("OK"));
    }
}
