package com.nio.tcpdataflowexample.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.nio.tcpdataflowexample.server.Server;
import com.nio.tcpdataflowexample.server.TcpServer;
import com.sun.tools.javac.util.List;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Map;

public class ControllableToUpperCaseServer implements Server {

    private final String HOSTNAME;
    private final Integer COMMAND_PORT;
    private final Integer DATA_PORT;

    private Selector selector;
    private final int BUFFER_CAPACITY = 65535;
    private ByteBuffer buffer;

    private Multimap<Integer, SocketChannel> clients;
    private Map<Integer, TcpServer> servers;

    public ControllableToUpperCaseServer(String hostname, int commandPort, int dataPort) {
        this.HOSTNAME = hostname;
        this.COMMAND_PORT = commandPort;
        this.DATA_PORT = dataPort;
    }

    @Override
    public void start() throws Exception {
        selector = Selector.open();
        buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
        clients = ArrayListMultimap.create();
        servers = new HashMap<>();

        TcpServer toUpperCaseTcpServer = new ToUpperCaseTcpServer(selector, buffer, clients)
                                                .hostname(HOSTNAME)
                                                .port(DATA_PORT)
                                                .build();

        List<Integer> manageablePorts = List.of(DATA_PORT);

        TcpServer commandTcpServer = new CommandTcpServer(selector, buffer, clients, manageablePorts)
                                                .hostname(HOSTNAME)
                                                .port(COMMAND_PORT)
                                                .build();

        servers.put(DATA_PORT, toUpperCaseTcpServer);
        servers.put(COMMAND_PORT, commandTcpServer);

        for(TcpServer server : servers.values()) {
            server.start();
        }

        while (!Thread.currentThread().isInterrupted()) {
            System.out.printf("Wait new events..%n");
            selector.select();

            for(TcpServer server : servers.values()) {
                server.handleRequest();
            }
        }
    }
}
