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

public class Set implements WriteCommand
{
    private BytesWrapper key;
    private BytesWrapper value;
    private long         timeout     = -1;
    private boolean      notExistSet = false;
    private boolean      existSet    = false;

    @Override
    public CommandType type()
    {
        return CommandType.set;
    }

    @Override
    public void setContent(Resp[] array)
    {
        key = ((BulkString) array[1]).getContent();
        value = ((BulkString) array[2]).getContent();
        int index = 3;
        while (index < array.length)
        {
            String string = ((BulkString) array[index]).getContent().toUtf8String();
            index++;
            if (string.startsWith("EX"))
            {
                String seconds = ((BulkString) array[index]).getContent().toUtf8String();
                timeout = Integer.parseInt(seconds) * 1000;
            }
            else if (string.startsWith("PX"))
            {
                String seconds = ((BulkString) array[index]).getContent().toUtf8String();
                timeout = Integer.parseInt(seconds);
            }
            else if (string.equals("NX"))
            {
                notExistSet = true;
            }
            else if (string.equals("XX"))
            {
                existSet = true;
            }
        }
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore)
    {
        if (notExistSet && redisCore.exist(key))
        {
            ctx.writeAndFlush(BulkString.NullBulkString);
        }
        else if (existSet && !redisCore.exist(key))
        {
            ctx.writeAndFlush(BulkString.NullBulkString);
        }
        else
        {
            if (timeout != -1)
            {
                timeout += System.currentTimeMillis();
            }
            RedisString stringData = new RedisString();
            stringData.setValue(value);
            stringData.setTimeout(timeout);
            redisCore.put(key, stringData);
            ctx.writeAndFlush(new SimpleString("OK"));
        }
    }

    @Override
    public void handle(RedisCore redisCore) {
        if (notExistSet && redisCore.exist(key))
        {

        }
        else if (existSet && !redisCore.exist(key))
        {

        }
        else
        {
            if (timeout != -1)
            {
                timeout += System.currentTimeMillis();
            }
            RedisString stringData = new RedisString();
            stringData.setValue(value);
            stringData.setTimeout(timeout);
            redisCore.put(key, stringData);

        }
    }
}
