package com.example.redisserver.command;



import com.example.redisserver.RedisCore;
import com.example.redisserver.resp.Resp;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author WesleyGo
 */
public interface Command
{
    Charset CHARSET = StandardCharsets.UTF_8;
    static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Command.class);


    /**
     * 获取接口类型
     * @return 接口类型
     */
    CommandType type();

    /**
     * 注入属性
     * @param array 操作数组
     */
    void setContent(Resp[] array);

    /**
     * 处理消息命令
     * @param ctx 管道
     * @param redisCore redis数据库
     */
    void handle(ChannelHandlerContext ctx, RedisCore redisCore);
}
