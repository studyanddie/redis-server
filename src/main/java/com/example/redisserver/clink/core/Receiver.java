package com.example.redisserver.clink.core;

import java.io.Closeable;
import java.io.IOException;

public interface Receiver extends Closeable {
    void setReceiveListener(IoArgs.IoArgsEventListener processor);

    boolean receiveAsync(IoArgs args) throws IOException;
}
