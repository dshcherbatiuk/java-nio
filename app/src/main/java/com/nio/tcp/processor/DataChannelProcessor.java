package com.nio.tcp.processor;

import com.nio.tcp.exception.DataChannelProcessingException;
import com.nio.tcp.model.ServerPort;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class DataChannelProcessor implements ChannelProcessor {

    private static ChannelProcessor dataChannelProcessorInstance;

    private DataChannelProcessor() {
    }

    public static ChannelProcessor getInstance() {
        if (dataChannelProcessorInstance == null) {
            dataChannelProcessorInstance = new DataChannelProcessor();
        }
        return dataChannelProcessorInstance;
    }

    @Override
    public ServerPort getPort() {
        return ServerPort.DATA_CHANNEL_PORT;
    }

    @Override
    public void process(ByteBuffer buffer, SocketChannel client) {
        for (int i = 0; i < buffer.limit(); i++) {
            buffer.put(i, (byte) Character.toUpperCase(buffer.get(i)));
        }
        try {
            client.write(buffer);
        } catch (IOException e) {
            throw new DataChannelProcessingException(e.getMessage());
        }
    }
}