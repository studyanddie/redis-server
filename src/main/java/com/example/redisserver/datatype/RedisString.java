package com.example.redisserver.datatype;

/**
 * @author wesleyGo
 */
public class RedisString implements RedisData
{
    private volatile long timeout;

    private BytesWrapper value;
    //构造方法默认都是用bytesWrapper作为顶层，相当于string
    public RedisString(BytesWrapper value){
        this.value = value;
        this.timeout = -1;
    }
    public RedisString(){

    }
    public BytesWrapper getValue()
    {
        return value;
    }

    public void setValue(BytesWrapper value)
    {
        this.value = value;
    }

    @Override
    public long timeout()
    {
        return timeout;
    }

    @Override
    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }
}
