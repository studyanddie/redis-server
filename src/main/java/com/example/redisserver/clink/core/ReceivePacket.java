package com.example.redisserver.clink.core;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 接收包的定义
 */
public abstract class ReceivePacket extends Packet{
    public abstract void save(byte[] bytes,int count);

}
