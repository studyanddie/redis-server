package com.example.redisserver.command.impl;

import com.example.redisserver.RedisCore;
import com.example.redisserver.command.Command;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.RespArray;
import io.netty.channel.ChannelHandlerContext;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Keys implements Command
{
    String pattern="";
    @Override
    public CommandType type()
    {
        return CommandType.keys;
    }

    @Override
    public void setContent(Resp[] array)
    {
        //需要转译的字符(    [     {    /    ^    -    $     ¦    }    ]    )    ?    *    +    .
        pattern= "."+((BulkString) array[1]).getContent().toUtf8String();

    }


    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        Set<BytesWrapper> keySet= redisCore.keys();

        Resp[] resps = keySet.stream().filter(k->{
            String content=null;
            try {
                 content=k.toUtf8String();
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return Pattern.matches(pattern, content);
        }).flatMap(key -> {
            Resp[] info = new Resp[1];
            info[0] = new BulkString(key);
            return Stream.of(info);
        }).toArray(Resp[]::new);
        ctx.writeAndFlush(new RespArray(resps));
    }
}