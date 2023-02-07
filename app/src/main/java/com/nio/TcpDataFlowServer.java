package com.nio;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class TcpDataFlowServer {

    private final ServerConfiguration config;
    private final DataProcessor dataProcessor;
    private final CommandProcessor commandProcessor;

    private Selector selector;
    private final ByteBuffer buffer;
    private final Multimap<Integer, SocketChannel> clients;

    //Do not create instances here but rely on abstractions (dependency inversion principle)
    public TcpDataFlowServer(ServerConfiguration config, DataProcessor dataProcessor, CommandProcessor commandProcessor) {
        this.config = config;
        this.dataProcessor = dataProcessor;
        this.commandProcessor = commandProcessor;
        this.buffer = ByteBuffer.allocate(65535);
        this.clients = ArrayListMultimap.create();
    }

    //main method was very big, extracted logical parts to methods to reduce complexity
    public void startServer() throws Exception {

        System.out.printf("Tcp Data Flow Example started at %s:%s%n", config.getHost(),
                String.join(",", config.getDataPort().toString(), config.getCommandPort().toString()));

        selector = Selector.open();
        config.configureServerSocketChannel(selector);

        while (!Thread.currentThread().isInterrupted()) {
            System.out.printf("Wait new events..%n");
            selector.select();

            final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            iterator.forEachRemaining(selectionKey -> {
                try {
                    if (selectionKey.isAcceptable()) {
                        acceptConnection(selectionKey);
                    }

                    if (selectionKey.isReadable()) {
                        System.out.println("Handle READ event");
                        final SocketChannel client = (SocketChannel) selectionKey.channel();
                        final int read = client.read(buffer);

                        if (read == -1) {
                            closeConnection(client);
                            return;
                        }
                        buffer.flip();

                        int localPort = client.socket().getLocalPort();

                        if (localPort == config.getDataPort()) {
                            client.write(dataProcessor.process(buffer));
                        } else if (localPort == config.getCommandPort()) {
                            final String command = StandardCharsets.UTF_8.decode(buffer).toString();
                            commandProcessor.processCommand(client, command, this::registerEvent);
                        }

                        buffer.clear();
                    }
                    iterator.remove();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void closeConnection(SocketChannel client) throws IOException {
        client.close();
        client.keyFor(selector).cancel();
        System.out.printf("The connection was closed: %s%n", client);
    }

    private void acceptConnection(SelectionKey selectionKey) throws IOException {
        System.out.println("Handle READ event");
        final ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());
        final SocketChannel client = server.accept();
        client.configureBlocking(false);
        System.out.printf("New connection accepted: %s%n", client);

        clients.put(server.socket().getLocalPort(), client);

        client.register(selector, SelectionKey.OP_READ);
    }

    private void registerEvent(final int op) {
        clients.get(config.getDataPort()).forEach(c -> {
            try {
                c.register(selector, op);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
