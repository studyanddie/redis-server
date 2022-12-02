package com.example.redisserver.command.impl.hash;



import com.example.redisserver.RedisCore;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.command.WriteCommand;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.datatype.RedisHash;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.RespInt;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Hdel implements WriteCommand
{
    private BytesWrapper key;
    private List<BytesWrapper> fields;

    @Override
    public CommandType type()
    {
        return CommandType.hdel;
    }

    @Override
    public void setContent(Resp[] array)
    {
        key = ((BulkString) array[1]).getContent();
        fields = Stream.of(array).skip(2).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        RedisHash redisHash = (RedisHash) redisCore.get(key);
        int       del       = redisHash.del(fields);
        ctx.writeAndFlush(new RespInt(del));
    }

    @Override
    public void handle(RedisCore redisCore) {
        RedisHash redisHash = (RedisHash) redisCore.get(key);
        redisHash.del(fields);
    }
}
