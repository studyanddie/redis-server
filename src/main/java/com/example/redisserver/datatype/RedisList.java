package com.example.redisserver.datatype;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WesleyGo
 */
public class RedisList implements RedisData
{
    private       long                timeout = -1;
    private final Deque<BytesWrapper> deque   = new LinkedList<>();

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

    public void lpush(BytesWrapper... values)
    {
        for (BytesWrapper value : values)
        {
            deque.addFirst(value);
        }
    }

    public int size()
    {
        return deque.size();
    }
    //从左加入数据
    public void lpush(List<BytesWrapper> values)
    {
        for (BytesWrapper value : values)
        {
            deque.addFirst(value);
        }
    }
    //从右加入数据
    public void rpush(List<BytesWrapper> values)
    {
        for (BytesWrapper value : values)
        {
            deque.addLast(value);
        }
    }

    //返回指定区间内的数据
    public List<BytesWrapper> lrang(int start, int end)
    {
        //skip跳过start个数据
        return deque.stream().skip(start).limit(end - start >= 0 ? end - start + 1 : 0).collect(Collectors.toList());
    }

    public int remove(BytesWrapper value)
    {
        int count = 0;
        while (deque.remove(value))
        {
            count++;
        }
        return count;
    }
}
