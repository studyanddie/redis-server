package com.example.redisserver.command.impl.string;


import com.example.redisserver.RedisCore;
import com.example.redisserver.command.Command;
import com.example.redisserver.command.CommandType;
import com.example.redisserver.datatype.BytesWrapper;
import com.example.redisserver.datatype.RedisData;
import com.example.redisserver.datatype.RedisString;
import com.example.redisserver.resp.BulkString;
import com.example.redisserver.resp.Resp;
import io.netty.channel.ChannelHandlerContext;

public class Get implements Command
{
    private BytesWrapper key;

    @Override
    public CommandType type()
    {
        return CommandType.get;
    }

    @Override
    public void setContent(Resp[] array)
    {
        key = ((BulkString) array[1]).getContent();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        RedisData redisData = redisCore.get(key);
        if (redisData == null)
        {
            ctx.writeAndFlush(BulkString.NullBulkString);
        }
        else if (redisData instanceof RedisString)
        {
            BytesWrapper value = ((RedisString) redisData).getValue();
            ctx.writeAndFlush(new BulkString(value));
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
}
