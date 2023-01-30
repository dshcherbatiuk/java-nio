package com.nio.registrator;

import com.nio.exception.PortRegistrationException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class ServerSocketPortRegistrator implements PortRegistrator {

    private final Selector selector;

    public ServerSocketPortRegistrator(final Selector selector) {
        this.selector = selector;
    }

    @Override
    public void registerPort(final Integer port, final String hostName) {
        try {
            final ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(hostName, port));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        } catch (final IOException e) {
            throw new PortRegistrationException(e.getMessage());
        }
    }
}
