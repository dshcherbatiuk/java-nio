package com.nio.tcp;

import com.nio.tcp.handler.EventHandler;
import com.nio.tcp.handler.SelectionKeyEventHandler;
import com.nio.tcp.initializer.ServerInitializer;
import com.nio.tcp.singleton.SelectorSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public final class TcpDataFlowExample {

    private static final String HOSTNAME = "0.0.0.0";

    private static final ServerInitializer SERVER_INITIALIZER = ServerInitializer.getInstance();
    private static final Selector SELECTOR = SelectorSingleton.getInstance();
    private static final EventHandler EVENT_HANDLER = SelectionKeyEventHandler.getInstance();

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpDataFlowExample.class);

    private TcpDataFlowExample() {
    }

    public static void main(String[] args) throws IOException {
        SERVER_INITIALIZER.initServer(HOSTNAME);

        while (!Thread.currentThread().isInterrupted()) {
            LOGGER.info("Wait new events..%n");
            SELECTOR.select();

            Iterator<SelectionKey> iterator = SELECTOR.selectedKeys().iterator();
            iterator.forEachRemaining(selectionKey -> {
                EVENT_HANDLER.process(selectionKey);
                iterator.remove();
            });
        }
    }
}