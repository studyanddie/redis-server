package com.example.redisserver;


import com.example.redisserver.aof.Aof;
import com.example.redisserver.channel.DefaultChannelSelectStrategy;
import com.example.redisserver.channel.LocalChannelOption;
import com.example.redisserver.channel.SingleSelectChannelOption;
import com.example.redisserver.util.CommandDecoder;
import com.example.redisserver.util.CommandHandler;
import com.example.redisserver.util.PropertiesUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

/**
 * @author WesleyGo
 */
public class MyRedisServer implements RedisServer
{
    private static final Logger LOGGER = Logger.getLogger(MyRedisServer.class);
    private final RedisCore redisCore =  new RedisCoreImpl();
    private final ServerBootstrap serverBootstrap=new ServerBootstrap();
    private final  EventExecutorGroup redisSingleEventExecutor;
    private final LocalChannelOption channelOption;
    private Aof aof;
    public MyRedisServer()
    {
        channelOption=new DefaultChannelSelectStrategy().select();
        this.redisSingleEventExecutor=new NioEventLoopGroup(1);
    }
    public MyRedisServer(LocalChannelOption channelOption)
    {
        this.channelOption=channelOption;
        this.redisSingleEventExecutor=new NioEventLoopGroup(1);
    }

    @Override
    public void start()
    {
        if(PropertiesUtil.getAppendOnly()) {
            aof=new Aof(this.redisCore);
        }
        start0();
    }

    @Override
    public void close()
    {
        try {
            channelOption.boss().shutdownGracefully();
            channelOption.selectors().shutdownGracefully();
            redisSingleEventExecutor.shutdownGracefully();
        }catch (Exception ignored) {
            LOGGER.warn( "Exception!", ignored);
        }
    }
    public void start0() {
        //启动类 负责组装netty组件，启动服务器
        serverBootstrap
                //group组 boss用来处理可连接事件 selectors用来处理可读事件
                .group(channelOption.boss(), channelOption.selectors())
                //使用nio实现
                .channel(channelOption.getChannelClass())
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                //false
                .option(ChannelOption.SO_KEEPALIVE, PropertiesUtil.getTcpKeepAlive())
//                .childOption(ChannelOption.TCP_NODELAY, true)
//                .childOption(ChannelOption.SO_SNDBUF, 65535)
//                .childOption(ChannelOption.SO_RCVBUF, 65535)
                .localAddress(new InetSocketAddress(PropertiesUtil.getNodeAddress(), PropertiesUtil.getNodePort()))
                //处理器 决定了读写执行哪些操作
                .childHandler(
                        //channel代表和客户端进行数据读写的通道 initiallizer 初始化，负责添加别的handler
                        new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //添加具体handler
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        channelPipeline.addLast(
                                new ResponseEncoder(),
                                new CommandDecoder(aof)//,
//                                /*心跳,管理长连接*/
//                                new IdleStateHandler(0, 0, 20)
                        );
                        channelPipeline.addLast(redisSingleEventExecutor,new CommandHandler(redisCore)) ;
                    }
                });

        try {
            ChannelFuture sync = serverBootstrap.bind().sync();
            LOGGER.info(sync.channel().localAddress().toString());
        } catch (InterruptedException e) {
//
            LOGGER.warn( "Interrupted!", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public RedisCore getRedisCore()
    {
        return redisCore;
    }

    public static void main(String[] args)
    {
        new MyRedisServer(new SingleSelectChannelOption()).start();
    }

}
