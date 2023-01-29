package com.nio;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

class ClientHelper {
    private static final String STOP_COMMAND = "stop-read";
    private static final String START_COMMAND = "start-read";
    private final Multimap<Integer, SocketChannel> clients = ArrayListMultimap.create();
    private final Selector selector;

    ClientHelper(Selector selector) {
        this.selector = selector;
    }

    void registerPort(String hostName, int portNumber) throws IOException {
        final ServerSocketChannel serverSocket = ServerSocketChannel.open()
                .bind(new InetSocketAddress(hostName, portNumber));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }

    void registerClient(SelectionKey selectionKey) throws IOException {
        final ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());
        final SocketChannel client = server.accept();
        client.configureBlocking(false);
        System.out.printf("New connection accepted: %s%n", client);

        clients.put(server.socket().getLocalPort(), client);
        client.register(selector, SelectionKey.OP_READ);
    }

    void registerEvents(SocketChannelWrapper client, String command, int port) throws IOException {
        switch (command) {
            case STOP_COMMAND: {
                System.out.println("Handle stop-read");
                registerEvents(0, port);
            }
            break;
            case START_COMMAND: {
                System.out.println("Handle start-read");
                registerEvents(SelectionKey.OP_READ, port);
            }
            break;
            default: {
                client.write(ByteBuffer.wrap(
                        String.format("Supported commands:%n%s%n%s%n", STOP_COMMAND, START_COMMAND).getBytes(StandardCharsets.UTF_8)
                ));
            }
        }
    }

    private void registerEvents(final int op, int port) {
        clients.get(port).forEach(c -> {
            try {
                c.register(selector, op);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}