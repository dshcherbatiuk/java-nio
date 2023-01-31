package com.nio.tcp.processor;

import com.nio.tcp.model.ServerPort;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface ChannelProcessor {
    ServerPort getPort();

    void process(ByteBuffer buffer, SocketChannel client);
}