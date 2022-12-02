package com.example.redisserver.resp;

/**
 * @author WesleyGo
 */
public class RespInt implements Resp
{
    int value;

    public RespInt(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
