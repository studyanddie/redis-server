package com.example.redisserver.clink.core;

/**
 * 发送的包定义
 */
public abstract class SendPacket extends Packet{
    public abstract byte[] bytes();

    public boolean isCanceled;

    /**
     * 设置取消发送标记
     */
    public boolean isCanceled(){
        return isCanceled;
    }

}
