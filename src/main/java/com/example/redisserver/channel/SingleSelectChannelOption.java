package com.example.redisserver.channel;

import com.example.redisserver.netty.channel.nio.NioSingleEventLoopGroup;
import com.example.redisserver.netty.channel.socket.NioSingleServerSocketChannel;
import io.netty.channel.EventLoopGroup;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleSelectChannelOption implements LocalChannelOption {
    private final NioSingleEventLoopGroup single;

    public SingleSelectChannelOption(NioSingleEventLoopGroup single) {
        this.single = single;
    }
    public SingleSelectChannelOption()
    {
        this.single = new NioSingleEventLoopGroup( new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Server_boss_" + index.getAndIncrement());
            }
        });

    }
    @Override
    public EventLoopGroup boss() {
        return  this.single;
    }

    @Override
    public EventLoopGroup selectors() {
        return  this.single;
    }

    @Override
    public Class getChannelClass() {
        return NioSingleServerSocketChannel.class;
    }
}
