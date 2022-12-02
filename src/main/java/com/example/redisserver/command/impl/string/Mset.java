package com.example.redisserver.command.impl.string;


import com.example.redisserver.RedisCore;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.command.WriteCommand;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.datatype.RedisString;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.SimpleString;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mset implements WriteCommand
{
    private List<BytesWrapper> kvList;

    @Override
    public CommandType type()
    {
        return CommandType.mset;
    }

    @Override
    public void setContent(Resp[] array)
    {
        kvList = Stream.of(array).skip(1).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        for (int i = 0; i<kvList.size();i+=2) {

            redisCore.put(kvList.get(i), new RedisString(kvList.get(i+1)));
        }
        ctx.writeAndFlush(SimpleString.OK);
    }

    @Override
    public void handle(RedisCore redisCore) {
        for (int i = 0; i<kvList.size();i+=2) {
            redisCore.put(kvList.get(i), new RedisString(kvList.get(i+1)));
        }
    }
}
