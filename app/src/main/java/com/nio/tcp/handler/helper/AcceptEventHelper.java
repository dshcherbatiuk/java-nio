package com.nio.tcp.handler.helper;

import com.google.common.collect.Multimap;
import com.nio.tcp.exception.AcceptConnectionException;
import com.nio.tcp.singleton.SelectorSingleton;
import com.nio.tcp.singleton.SocketChannelsSingletone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptEventHelper implements EventHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptEventHelper.class);
    private static EventHelper acceptEventHelperInstance;
    private final Selector selector;
    private final Multimap<Integer, SocketChannel> socketChannels;

    private AcceptEventHelper() {
        this.selector = SelectorSingleton.getInstance();
        this.socketChannels = SocketChannelsSingletone.getInstance();
    }

    public static EventHelper getInstance() {
        if (acceptEventHelperInstance == null) {
            acceptEventHelperInstance = new AcceptEventHelper();
        }
        return acceptEventHelperInstance;
    }

    @Override
    public void processEvent(SelectionKey selectionKey) {
        LOGGER.info("Handle READ event");
        try {
            ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            LOGGER.info("New connection accepted: {}", client);

            socketChannels.put(server.socket().getLocalPort(), client);

            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new AcceptConnectionException(e.getMessage());
        }
    }
}