package com.example.redisserver.channel;


import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

/**
 * 建立TCP连接 对连接传输数据进行处理
 * @param <C>
 */
public interface LocalChannelOption< C extends Channel>  {
    /**
     * @return 返回获取tcp线程
     */
    EventLoopGroup boss();
    /**
     * @return 返回处理tcp线程
     */
    EventLoopGroup selectors();
    /**
     * @return 返回管道类型
     */
    Class<? extends C> getChannelClass();
}
