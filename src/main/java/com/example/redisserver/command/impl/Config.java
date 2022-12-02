package com.example.redisserver.command.impl;

import com.example.redisserver.RedisCore;
import com.example.redisserver.command.Command;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.RespArray;
import com.example.redisserver.util.TRACEID;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public class Config implements Command
{
    private String param;

    @Override
    public CommandType type()
    {
        return CommandType.config;
    }

    @Override
    public void setContent(Resp[] array)
    {
        if (array.length != 3)
        {
            throw new IllegalStateException();
        }
        if (((BulkString) array[1]).getContent().toUtf8String().equals("get") == false)
        {
            throw new IllegalStateException();
        }
        param = ((BulkString) array[2]).getContent().toUtf8String();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        if (param.equals("*") || param.equals("databases"))
        {
            List<BulkString> list = new ArrayList<>();
            list.add(new BulkString(new BytesWrapper("databases".getBytes(CHARSET))));
            list.add(new BulkString(new BytesWrapper("1".getBytes(CHARSET))));
            Resp[]    array  = list.toArray(new Resp[list.size()]);
            RespArray arrays = new RespArray(array);
            ctx.writeAndFlush(arrays);
        }
        else
        {
            String traceId = TRACEID.currentTraceId();
            LOGGER.debug("traceId:"+traceId+" 不识别的Config命令模式:"+param );
            ctx.channel().close();
        }
    }
}
