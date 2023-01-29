package com.nio.tcpdataflowexample.impl;

import com.google.common.collect.Multimap;
import com.nio.tcpdataflowexample.server.MutualTcpServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class ToUpperCaseTcpServer extends MutualTcpServer {

    public ToUpperCaseTcpServer(Selector selector, ByteBuffer buffer, Multimap<Integer, SocketChannel> clients) {
        super(selector, buffer, clients);
    }

    @Override
    public void handleReadEvent(SelectionKey selectionKey) throws IOException {
        final SocketChannel client = (SocketChannel) selectionKey.channel();

        if (PORT.equals(client.socket().getLocalPort())) {
            for (int i = 0; i < buffer.limit(); i++) {
                buffer.put(i, (byte) Character.toUpperCase(buffer.get(i)));
            }
            client.write(buffer);
        }
    }
}
