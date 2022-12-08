package com.example.redisserver;


import com.example.redisserver.aof.Aof;
import com.example.redisserver.channel.DefaultChannelSelectStrategy;
import com.example.redisserver.channel.LocalChannelOption;
import com.example.redisserver.channel.SingleSelectChannelOption;
import com.example.redisserver.heartbeat.ServerHandler;
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
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

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

    /**
     * Redis服务器的启动类，它负责初始化服务器所需的组件，并启动服务器。
     *
     * 首先，它使用serverBootstrap组装了一组Netty组件，并设置了一些参数，如线程池大小、心跳间隔时间等。
     *
     * 然后，它指定了服务器监听的地址和端口。
     *
     * 最后，它定义了一个ChannelInitializer，用来初始化服务器的通道，并添加了一些处理器，如编码器、解码器和命令处理器等。
     *
     * 最后，它调用serverBootstrap.bind()方法来启动服务
     */
    public void start0() {
        //启动类 负责组装netty组件，启动服务器
        serverBootstrap
                //group组 boss用来处理可连接事件 selectors用来处理可读事件
                .group(channelOption.boss(), channelOption.selectors())
                //使用nio实现
                .channel(channelOption.getChannelClass())
                //只会记录INFO级别以上的日志，比如警告日志和错误日志
                .handler(new LoggingHandler(LogLevel.INFO))
                //接收队列的长度为1024
                .option(ChannelOption.SO_BACKLOG, 1024)
                /**
                 * 这个套接字选项通知内核，如果端口忙，但TCP状态位于 TIME_WAIT ，
                 * 可以重用端口。如果端口忙，而TCP状态位于其他状态，重用端口时依旧得到一个错误信息，
                 * 指明"地址已经使用中"。如果你的服务程序停止后想立即重启，而新套接字依旧使用同一端口，
                 * 此时SO_REUSEADDR 选项非常有用
                 */
                .option(ChannelOption.SO_REUSEADDR, true)
                //false,不开启keepalive
                .option(ChannelOption.SO_KEEPALIVE, PropertiesUtil.getTcpKeepAlive())

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
                                new CommandDecoder(aof),
//                                /*心跳,管理长连接*/
                                new IdleStateHandler(3, 5, 7, TimeUnit.MINUTES),
                                new ServerHandler()
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
