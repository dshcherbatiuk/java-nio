package com.nio.event.handler;

import com.google.common.collect.Multimap;
import com.nio.exception.ConnectionAcceptingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class SelectionKeyAcceptEventHandler implements SelectionKeyEventHandler {

    private final Selector selector;
    private final Multimap<Integer, SocketChannel> clients;
    private final Logger logger = LoggerFactory.getLogger(SelectionKeyAcceptEventHandler.class);

    public SelectionKeyAcceptEventHandler(final Selector selector, final Multimap<Integer, SocketChannel> clients) {
        this.selector = selector;
        this.clients = clients;
    }

    @Override
    public void handle(final SelectionKey selectionKey) {
        try {
            logger.info("Handle READ event");
            final ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());
            final SocketChannel client = server.accept();
            client.configureBlocking(false);
            logger.info("New connection accepted: {}", client);

            clients.put(server.socket().getLocalPort(), client);

            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new ConnectionAcceptingException(e.getMessage());
        }
    }
}
