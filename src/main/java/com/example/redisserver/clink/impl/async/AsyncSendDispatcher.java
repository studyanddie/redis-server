package com.example.redisserver.clink.impl.async;


import com.example.redisserver.clink.core.*;
import com.example.redisserver.clink.utils.CloseUtils;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncSendDispatcher implements SendDispatcher{
    private final Sender sender;
    private final Queue<SendPacket> queue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isSending = new AtomicBoolean();
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    
    private IoArgs ioArgs = new IoArgs();
    private SendPacket packetTemp;
    private int total;
    private int position;
    public AsyncSendDispatcher(Sender sender) {
        this.sender = sender;

    }

    /**
     * 发送Packet
     * 首先添加到队列，如果当前状态为未启动发送状态
     * 则，尝试让reader提取一份packet进行数据发送
     * <p>
     * 如果提取数据后reader有数据，则进行异步输出注册
     *
     * @param packet 数据
     */
    @Override
    public void send(SendPacket packet) {

            queue.offer(packet);
            if (isSending.compareAndSet(false, true)) {
                sendNextPacket();
            }

    }




    private SendPacket takePacket(){
        SendPacket packet = queue.poll();
        if(packet!=null && packet.isCanceled()){
            //已取消不用发送
            return takePacket();
        }
       return packet;
    }

    private void sendNextPacket() {
        SendPacket temp = packetTemp;
        if(temp !=null){
            CloseUtils.close(temp);
        }
        SendPacket packet = packetTemp = takePacket();
        if(packet==null){
            isSending.set(false);
            return;
        }
        total = packet.length();
        position = 0;
        sendCurrentPacket();
    }

    private void sendCurrentPacket() {
            IoArgs args = ioArgs;
            args.startWriting();

            if(position>=total){
                sendNextPacket();
                return;
            }else if(position == 0){
                //首包 需要携带长度信息;
                args.writeLength(total);
            }

            byte[] bytes = packetTemp.bytes();
            int count = args.readFrom(bytes,position);
            position+=count;

            args.finishWriting();
        try {
            sender.sendAsync(args,ioArgsEventListener);
        } catch (IOException e) {
            closeAndNotify();
        }
    }

    private void closeAndNotify() {
        CloseUtils.close(this);
    }

    private final IoArgs.IoArgsEventListener ioArgsEventListener = new IoArgs.IoArgsEventListener() {
        @Override
        public void onStarted(IoArgs args) {

        }

        @Override
        public void onCompleted(IoArgs args) {
            sendCurrentPacket();
        }
    };
    @Override
    public void close() throws IOException {
        if (isClosed.compareAndSet(false, true)) {
            isSending.set(false);
            SendPacket packet = this.packetTemp;
            if(packet!=null){
                packetTemp = null;
                CloseUtils.close(packet);
            }
        }
    }
    @Override
    public void cancel(SendPacket packet) {

    }
}
