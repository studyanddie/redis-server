package com.example.redisserver.command.impl;


import com.example.redisserver.RedisCore;
import com.example.redisserver.command.Command;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import io.netty.channel.ChannelHandlerContext;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Info implements Command
{
    @Override
    public CommandType type()
    {
        return CommandType.info;
    }

    @Override
    public void setContent(Resp[] array)
    {
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        List<String> list = new ArrayList<>();
        list.add("redis_version:jfire_redis_mock");
        list.add("os:" + System.getProperty("os.name"));
        list.add("process_id:" + getPid());
        Optional<String> reduce = list.stream().map(name -> name + "\r\n").reduce((first, second) -> first + second);
        String           s      = reduce.get();
        ctx.writeAndFlush(new BulkString(new BytesWrapper(s.getBytes(CHARSET))));
    }

    private String getPid()
    {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid  = name.split("@")[0];
        return pid;
    }
}
