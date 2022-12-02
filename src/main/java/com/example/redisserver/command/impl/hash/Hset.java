package com.example.redisserver.command.impl.hash;



import com.example.redisserver.RedisCore;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.command.WriteCommand;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.datatype.RedisData;
import com.example.redisserver.datatype.RedisHash;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import com.example.redisserver.resp.RespInt;
import io.netty.channel.ChannelHandlerContext;

public class Hset implements WriteCommand
{
    private BytesWrapper key;
    private BytesWrapper field;
    private BytesWrapper value;

    @Override
    public CommandType type()
    {
        return CommandType.hset;
    }

    @Override
    public void setContent(Resp[] array)
    {
        key = ((BulkString) array[1]).getContent();
        field = ((BulkString) array[2]).getContent();
        value = ((BulkString) array[3]).getContent();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        RedisData redisData = redisCore.get(key);
        if (redisData == null)
        {
            RedisHash redisHash = new RedisHash();
            int       put       = redisHash.put(field, value);
            redisCore.put(key, redisHash);
            ctx.writeAndFlush(new RespInt(put));
        }
        else if (redisData instanceof RedisHash)
        {
            RedisHash redisHash = (RedisHash) redisData;
            int       put       = redisHash.put(field, value);
            ctx.writeAndFlush(new RespInt(put));
        }
        else
        {
            throw new IllegalArgumentException("类型错误");
        }
    }

    @Override
    public void handle(RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null)
        {
            RedisHash redisHash = new RedisHash();
            redisHash.put(field, value);
            redisCore.put(key, redisHash);
        }
        else if (redisData instanceof RedisHash)
        {
            RedisHash redisHash = (RedisHash) redisData;
             redisHash.put(field, value);
        }
        else
        {
            throw new IllegalArgumentException("类型错误");
        }
    }
}
