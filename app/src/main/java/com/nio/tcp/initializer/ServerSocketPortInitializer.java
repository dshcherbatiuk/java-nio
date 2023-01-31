package com.nio.tcp.initializer;

import com.nio.tcp.exception.PortInitializationException;
import com.nio.tcp.singleton.SelectorSingleton;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class ServerSocketPortInitializer implements PortInitializer {

    private static ServerSocketPortInitializer serverSocketPortInitializerInstance;

    private final Selector selector;

    public ServerSocketPortInitializer() {
        this.selector = SelectorSingleton.getInstance();
    }

    public static ServerSocketPortInitializer getInstance() {
        if (serverSocketPortInitializerInstance == null) {
            serverSocketPortInitializerInstance = new ServerSocketPortInitializer();
        }
        return serverSocketPortInitializerInstance;
    }

    @Override
    public void initPort(Integer port, String hostName) {
        try {
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(hostName, port));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new PortInitializationException(e.getMessage());
        }
    }
}