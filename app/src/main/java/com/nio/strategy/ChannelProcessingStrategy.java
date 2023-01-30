package com.nio.strategy;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface ChannelProcessingStrategy {
    Integer getChannelPort();
    void process(ByteBuffer buffer, SocketChannel client);
}
