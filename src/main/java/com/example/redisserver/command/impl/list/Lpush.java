package com.example.redisserver.command.impl.list;


import com.example.redisserver.command.CommandType;
import com.example.redisserver.command.impl.Push;
import com.example.redisserver.datatype.RedisList;

public class Lpush extends Push
{

    public Lpush()
    {
        super(RedisList::lpush);
    }

    @Override
    public CommandType type()
    {
        return CommandType.lpush;
    }
}
