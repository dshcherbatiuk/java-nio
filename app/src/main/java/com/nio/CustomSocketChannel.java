package com.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class CustomSocketChannel implements SocketChannelWrapper {
    private final SocketChannel channel;

    public CustomSocketChannel(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void write(ByteBuffer buffer) throws IOException {
        channel.write(buffer);
    }

    @Override
    public int read(ByteBuffer buffer) throws IOException {
        return channel.read(buffer);
    }

    @Override
    public void closeChannel(Selector selector) throws IOException {
        channel.close();
        channel.keyFor(selector).cancel();
        System.out.printf("The connection was closed: %s%n", channel);
    }

    @Override
    public int getLocalPort() {
        return channel.socket().getLocalPort();
    }
}
