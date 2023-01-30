package com.nio.strategy;

import com.nio.DataFlowServerPort;
import com.nio.exception.DataChannelProcessingException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class DataChannelProcessingStrategy implements ChannelProcessingStrategy {

    @Override
    public Integer getChannelPort() {
        return DataFlowServerPort.DATA_CHANNEL_PORT.getPortNumber();
    }

    @Override
    public void process(final ByteBuffer buffer, final SocketChannel client) {
        for (int i = 0; i < buffer.limit(); i++) {
            buffer.put(i, (byte) Character.toUpperCase(buffer.get(i)));
        }
        try {
            client.write(buffer);
        } catch (final IOException e) {
            throw new DataChannelProcessingException(e.getMessage());
        }
    }
}
