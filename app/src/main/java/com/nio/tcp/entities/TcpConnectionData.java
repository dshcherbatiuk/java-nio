package com.nio.tcp.entities;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TcpConnectionData {

    public static final int BUFFER_CAPACITY = 65535;

    private final ByteBuffer buffer;
    private final Selector selector;
    private final Multimap<Integer, SocketChannel> clients;

    public TcpConnectionData(final String hostName) throws IOException {
        this.selector = initializeSelector(hostName);
        this.buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
        this.clients = ArrayListMultimap.create();
    }

    public Selector getSelector() {
        return selector;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public Multimap<Integer, SocketChannel> getClients() {
        return clients;
    }

    private Selector initializeSelector(String hostName) throws IOException {
        Selector selector = Selector.open();
        for (final int port : Port.getAvailablePorts()) {
            try(final ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
                serverSocket.bind(new InetSocketAddress(hostName, port));
                serverSocket.configureBlocking(false);
                serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            }
        }
        return selector;
    }
}
