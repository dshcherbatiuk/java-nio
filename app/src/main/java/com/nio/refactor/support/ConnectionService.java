package com.nio.refactor.support;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.nio.refactor.enums.Ports;
import com.nio.refactor.support.Buffer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ConnectionService {
    private Multimap<Integer, SocketChannel> clients;
    private static Selector selector;

    public ConnectionService(final String HOSTNAME) throws IOException {
        try {
            this.clients = ArrayListMultimap.create();
            this.selector = connectSelector(HOSTNAME);

        } catch (IOException e) {
            System.out.println("IOException in TcpDataFlowExample constructor");
            e.printStackTrace();
        }
    }

    public Multimap<Integer, SocketChannel> getClients() {
        return clients;
    }
    public Selector getSelector(){
        return selector;
    }

    public Selector connectSelector(String HOSTNAME) throws IOException {
        for (final int port : Ports.getAdress()) {
            try (final ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
                serverSocket.bind(new InetSocketAddress(HOSTNAME, port));
                serverSocket.configureBlocking(false);
                serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            }
        }
        return selector;
    }
}
