package com.example.redisserver.command.impl.string;


import com.example.redisserver.RedisCore;
import com.example.redisserver.command.Command;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.datatype.RedisData;
import com.example.redisserver.datatype.RedisString;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.RespArray;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mget implements Command
{
    private List<BytesWrapper> keys;

    @Override
    public CommandType type()
    {
        return CommandType.mget;
    }

    @Override
    public void setContent(Resp[] array)
    {
        keys = Stream.of(array).skip(1).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        LinkedList<BytesWrapper> linkedList= new LinkedList();
        keys.forEach(key -> {
            RedisData redisData = redisCore.get(key);
            if (redisData == null)
            {
            }
            else if (redisData instanceof RedisString)
            {
                linkedList.add(((RedisString) redisData).getValue()) ;
            }
            else
            {
                throw new UnsupportedOperationException();
            }
        });
        RespArray respArray = new RespArray(linkedList.stream().map(BulkString::new).toArray(Resp[]::new));
        ctx.writeAndFlush(respArray);
    }

}
