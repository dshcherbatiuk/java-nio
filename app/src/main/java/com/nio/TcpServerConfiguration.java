package com.nio;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

//extract configuration to separate class to decouple it from main logic
//covers single responsibility principle, allows easily change server configuration without refactoring logic
public class TcpServerConfiguration implements ServerConfiguration {

    private static final int COMMAND_PORT = 4444;
    private static final int DATA_PORT = 5555;
    private static final String HOSTNAME = "0.0.0.0";
    private static final int[] PORTS = new int[] { DATA_PORT, COMMAND_PORT };

    @Override
    public void configureServerSocketChannel(Selector selector) throws Exception  {

        for (final int port : PORTS) {
            final ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(HOSTNAME, port));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        }
    }

    @Override
    public String getHost() {
        return HOSTNAME;
    }

    @Override
    public Integer getCommandPort() {
        return COMMAND_PORT;
    }

    @Override
    public Integer getDataPort() {
        return DATA_PORT;
    }

}
