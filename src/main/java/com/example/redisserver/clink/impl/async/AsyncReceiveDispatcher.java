package com.example.redisserver.clink.impl.async;



import com.example.redisserver.clink.box.StringReceivePacket;
import com.example.redisserver.clink.core.IoArgs;
import com.example.redisserver.clink.core.ReceiveDispatcher;
import com.example.redisserver.clink.core.ReceivePacket;
import com.example.redisserver.clink.core.Receiver;
import com.example.redisserver.clink.utils.CloseUtils;
import com.example.redisserver.resp.Resp;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 接收调度
 */
public class AsyncReceiveDispatcher implements ReceiveDispatcher{
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private final Receiver receiver;
    private final ReceivePacketCallback callback;

    private IoArgs  ioArgs = new IoArgs();
    private ReceivePacket packetTemp;
    private byte[] buffer;
    private int total;
    private int position;
    public AsyncReceiveDispatcher(Receiver receiver, ReceivePacketCallback callback) {
        this.receiver = receiver;
        this.receiver.setReceiveListener(ioArgsEventListener);
        this.callback = callback;
    }

    /**
     * 开始进入接收方法
     */
    @Override
    public void start() {
        registerReceive();
    }

    /**
     * 停止接收数据
     */
    @Override
    public void stop() {

    }

    /**
     * 关闭操作，关闭相关流
     */
    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            ReceivePacket packet = this.packetTemp;
            if(packet!=null){
                packetTemp=null;
                CloseUtils.close(packet);
            }
        }
    }

    /**
     * 自主发起的关闭操作，并且需要进行通知
     */
    private void closeAndNotify() {
        CloseUtils.close(this);
    }

    /**
     * 注册接收数据
     */
    private void registerReceive() {
        try {
            receiver.receiveAsync(ioArgs);
        } catch (IOException e) {
            closeAndNotify();
        }
    }

    private IoArgs.IoArgsEventListener ioArgsEventListener = new IoArgs.IoArgsEventListener() {
        @Override
        public void onStarted(IoArgs args) {
            int receiveSize;
            if(packetTemp==null){
                receiveSize = 4;
            }else {
                receiveSize = Math.min(total - position,args.capacity());
            }
            args.limit(receiveSize);
        }
        //当有数据到达
        @Override
        public void onCompleted(IoArgs args) {
            assemblePacket(args);
            //继续接受下一条
            registerReceive();
        }


    };
    /**
     * 解析数据到Packet
     * @param args
     */
    private void assemblePacket(IoArgs args) {
        if(packetTemp==null){
            int length = args.readLength();
            packetTemp = new StringReceivePacket(length);
            buffer = new byte[length];
            total = length;
            position = 0;
        }
        int count = args.writeTo(buffer,0);
        if(count > 0){
            packetTemp.save(buffer,count);
            position+=count;

            if(position == total){
                ByteBuf byteBuf = Unpooled.wrappedBuffer(buffer);
                Resp.decode(byteBuf);
                completePacket();
                packetTemp=null;
            }
        }
    }
    //完成数据接受
    private void completePacket() {
        ReceivePacket packet = this.packetTemp;

        CloseUtils.close(packet);
        callback.onReceivePacketCompleted(packet);
    }
}
