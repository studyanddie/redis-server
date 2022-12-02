package com.example.redisserver.channel;

import com.example.redisserver.channel.epoll.EpollChannelOption;
import com.example.redisserver.channel.kqueue.KqueueChannelOption;
import com.example.redisserver.channel.select.NioSelectChannelOption;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.kqueue.KQueue;

public class DefaultChannelSelectStrategy implements ChannelSelectStrategy {
    @Override
    public LocalChannelOption select(){

        if(KQueue.isAvailable()){
            return new KqueueChannelOption();
        }
        if(Epoll.isAvailable()){
            return new EpollChannelOption();
        }
        return new NioSelectChannelOption();
    }
}
