package com.nio.tcpdataflowexample.server;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public abstract class MutualTcpServer extends TcpServer {

    protected Selector selector;
    protected ByteBuffer buffer;
    protected Multimap<Integer, SocketChannel> clients;

    private final int BUFFER_CAPACITY = 65535;

    public MutualTcpServer(Selector selector, ByteBuffer buffer, Multimap<Integer, SocketChannel> clients) {
        this.selector = selector;
        this.buffer = buffer;
        this.clients = clients;
    }

    @Override
    public void start() throws Exception {
        initializeFields();
        final ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(HOSTNAME, PORT));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void initializeFields() throws IOException {
        if (selector == null) {
            selector = Selector.open();
        }
        if (buffer == null) {
            buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
        }
        if (clients == null) {
            clients = ArrayListMultimap.create();
        }
    }

    public void handleRequest() throws IOException {
        final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

        while(iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();

            if (selectionKey.isAcceptable()) {
                final ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());

                if(PORT.equals(server.socket().getLocalPort())) {
                    handleNewConnection(selectionKey);
                    iterator.remove();
                }
            }

            if (selectionKey.isReadable()) {
                final SocketChannel client = (SocketChannel) selectionKey.channel();

                if(PORT.equals(client.socket().getLocalPort())) {
                    System.out.println("Handle READ event");

                    try {
                        final int read = client.read(buffer);

                        if (read == -1) {
                            stop(client);
                            iterator.remove();
                            return;
                        }
                    }catch (SocketException ex) {
                        stop(client);
                        iterator.remove();
                        return;
                    }

                    buffer.flip();

                    handleReadEvent(selectionKey);

                    buffer.clear();
                    iterator.remove();
                }
            }
        }
    }

    protected void handleNewConnection(SelectionKey selectionKey) throws IOException {
        System.out.println("Handle new connection event");

        final ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());
        final SocketChannel client = server.accept();
        client.configureBlocking(false);

        System.out.printf("New connection accepted: %s%n", client);

        clients.put(server.socket().getLocalPort(), client);

        client.register(selector, SelectionKey.OP_READ);
    }

    protected void stop(SocketChannel client) throws IOException {
        client.close();
        client.keyFor(selector).cancel();
        buffer.clear();
        System.out.printf("The connection was closed: %s%n", client);
    }

    protected abstract void handleReadEvent(SelectionKey selectionKey) throws IOException;
}