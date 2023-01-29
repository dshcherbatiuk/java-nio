package com.nio.tcpdataflowexample.impl;

import com.google.common.collect.Multimap;
import com.nio.tcpdataflowexample.server.MutualTcpServer;
import com.nio.tcpdataflowexample.command.Command;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.nio.tcpdataflowexample.command.Command.*;

public class CommandTcpServer extends MutualTcpServer {

    private final List<Integer> manageablePorts;

    public CommandTcpServer(Selector selector, ByteBuffer buffer, Multimap<Integer, SocketChannel> clients, List<Integer> manageablePorts) {
        super(selector, buffer, clients);
        this.manageablePorts = manageablePorts;
    }

    @Override
    public void handleReadEvent(SelectionKey selectionKey) throws IOException {
        final SocketChannel client = (SocketChannel) selectionKey.channel();

        if (PORT.equals(client.socket().getLocalPort())) {
            Command command = getCommandFromString(StandardCharsets.UTF_8.decode(buffer).toString());

            switch (command) {
                case STOP_READ:
                    System.out.printf("Handle %s%n", STOP_READ);
                    registerOperation(0);
                    return;
                case START_READ:
                    System.out.printf("Handle %s%n", START_READ);
                    registerOperation(SelectionKey.OP_READ);
                    return;
                case EXIT:
                    for(Integer port : manageablePorts) {
                        for (SocketChannel socketChannel : clients.get(port)) {
                            socketChannel.close();
                            socketChannel.keyFor(selector).cancel();
                        }
                    }
                    client.close();
                    client.keyFor(selector).cancel();
                    System.out.printf("The connection was closed: %s%n", client);
                    Thread.currentThread().interrupt();
                    return;

                default:
                    final byte[] unknownCommand = String.format("Supported commands:%n%s%n", getCommandsListAsString())
                            .getBytes(StandardCharsets.UTF_8);

                    client.write(ByteBuffer.wrap(unknownCommand));
            }
        }
    }

    private void registerOperation(final int operation) throws ClosedChannelException {
        for(Integer port : manageablePorts) {
            for (SocketChannel socketChannel : clients.get(port)) {
                socketChannel.register(selector, operation);
            }
        }
    }
}
