package com.example.redisserver.clink.box;

import com.example.redisserver.clink.core.ReceivePacket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 字符串接收包
 */
public class StringReceivePacket extends ReceivePacket {
    private byte[] buffer;
    private int position;
    public StringReceivePacket(int len) {
        buffer = new byte[len];
        length = len;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    @Override
    public void save(byte[] bytes, int count) {
        System.arraycopy(bytes,0,buffer,position,count);
        position+=count;
    }

    public String string(){
        return new String(buffer);
    }

    @Override
    public void close() throws IOException {

    }
}