package com.example.redisserver;



import com.example.redisserver.resp.Resp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ResponseEncoder extends MessageToByteEncoder<Resp> //WriteProcessor<Resp>
{

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Resp resp, ByteBuf byteBuf) throws Exception {
        try {
            Resp.write(resp,byteBuf);//msg.encode();
            byteBuf.writeBytes(byteBuf);
            // logger.info(byteBuffer.toString());
        }catch(Exception e) {
            channelHandlerContext.close();
        }
    }

}
