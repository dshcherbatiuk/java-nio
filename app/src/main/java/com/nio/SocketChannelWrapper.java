package com.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;

public interface SocketChannelWrapper {
    void write(ByteBuffer buffer) throws IOException;

    int read(ByteBuffer buffer) throws IOException;

    void closeChannel(Selector selector) throws IOException;

    int getLocalPort();
}
