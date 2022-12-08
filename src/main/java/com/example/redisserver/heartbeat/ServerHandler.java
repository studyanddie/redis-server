package com.example.redisserver.heartbeat;

import com.example.redisserver.aof.Aof;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Aof.class);
    /**
     *
     * @param ctx 上下文
     * @param evt 事件
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){

            //将evt向下转型
            IdleStateEvent event= (IdleStateEvent) evt;
            String eventType = null;
            switch (event.state()){
                case READER_IDLE:
                    eventType="读空闲";
                    break;
                case  WRITER_IDLE:
                    eventType="写空闲";
                    break;
                case ALL_IDLE:
                    eventType="读写空闲";
                    break;
            }
            System.out.println(ctx.channel().remoteAddress()+"--超时事件发生--"+eventType);
            LOGGER.info(ctx.channel().remoteAddress()+"--超时事件发生--"+eventType);
        }
    }
}
