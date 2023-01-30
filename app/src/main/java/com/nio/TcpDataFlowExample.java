package com.nio;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.nio.controller.Channel;
import com.nio.controller.ChannelOperationExecutor;
import com.nio.controller.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;

public final class TcpDataFlowExample {

    public static final String HOSTNAME = "0.0.0.0";
    public static final int BUFFER_CAPACITY = 65535;
    private static final Logger log = LoggerFactory.getLogger(TcpDataFlowExample.class);

    private TcpDataFlowExample() {
    }

    public static void main(final String... args) throws Exception {
        String ports = String.join(",", Channel.getPorts());
        log.info("Tcp Data Flow Example started at {}:{}", HOSTNAME, ports);
        try (Selector selector = Selector.open()) {
            prepareSelector(selector);
            final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
            final Multimap<Integer, SocketChannel> clients = ArrayListMultimap.create();
            while (!Thread.currentThread().isInterrupted()) {
                log.info("Wait new events..%n");
                selector.select();
                final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                iterator.forEachRemaining(selectionKey -> processSelectionKey(selectionKey, clients, buffer, selector, iterator));
            }
        }
    }

    private static void prepareSelector(final Selector selector) throws IOException {
        for (final Channel channel : Channel.values()) {
            try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
                serverSocket.bind(new InetSocketAddress(HOSTNAME, channel.getPort()));
                serverSocket.configureBlocking(false);
                serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            }
        }
    }

    private static void processSelectionKey(final SelectionKey selectionKey, final Multimap<Integer, SocketChannel> clients,
                                            final ByteBuffer buffer, final Selector selector, final Iterator<SelectionKey> iterator) {
        try {
            if (selectionKey.isAcceptable()) {
                log.info("Handle READ event");
                final ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());
                final SocketChannel client = server.accept();
                client.configureBlocking(false);
                log.info("New connection accepted: {}", client);

                clients.put(server.socket().getLocalPort(), client);

                client.register(selector, SelectionKey.OP_READ);
            }

            if (selectionKey.isReadable()) {
                log.info("Handle READ event");
                final SocketChannel client = (SocketChannel) selectionKey.channel();
                final int read = client.read(buffer);

                if (read == -1) {
                    client.close();
                    client.keyFor(selector).cancel();
                    log.info("The connection was closed: {}", client);
                    return;
                }
                buffer.flip();
                Operations operations = new Operations(client, buffer, selector, clients);
                doOperation(operations, client);
                buffer.clear();
            }
            iterator.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void doOperation(final Operations operations, final SocketChannel client) {
        ChannelOperationExecutor channelOperation = new ChannelOperationExecutor();
        switch (Objects.requireNonNull(Channel.getChannelByPort(client.socket().getLocalPort()))) {
            case DATA_CHANNEL:
                channelOperation.executeOperation(operations::write);
                break;
            case CONTROL_CHANNEL:
                channelOperation.executeOperation(operations::control);
                break;
        }
    }
}
