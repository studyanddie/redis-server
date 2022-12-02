package com.example.redisserver.command.impl.list;


import com.example.redisserver.RedisCore;
import com.example.redisserver.command.Command;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.datatype.RedisList;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.RespArray;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class Lrange implements Command
{
    BytesWrapper key;
    int          start;
    int          end;

    @Override
    public CommandType type()
    {
        return CommandType.lrange;
    }

    @Override
    public void setContent(Resp[] array)
    {
        key = ((BulkString) array[1]).getContent();
        start = Integer.parseInt(((BulkString) array[2]).getContent().toUtf8String());
        end = Integer.parseInt(((BulkString) array[3]).getContent().toUtf8String());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        RedisList redisList = (RedisList) redisCore.get(key);
        List<BytesWrapper> lrang     = redisList.lrang(start, end);
        RespArray respArray = new RespArray(lrang.stream().map(BulkString::new).toArray(Resp[]::new));
        ctx.writeAndFlush(respArray);
    }
}
