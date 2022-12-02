package com.example.redisserver.command.impl;


import com.example.redisserver.command.CommandType;
import com.example.redisserver.datatype.RedisList;

public class Rpush extends Push
{

    public Rpush()
    {
        super(RedisList::rpush);
    }

    @Override
    public CommandType type()
    {
        return CommandType.rpush;
    }
}
