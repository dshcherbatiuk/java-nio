package com.nio.eventhadler;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.nio.poller.PollerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptEventHandler implements EventHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(AcceptEventHandler.class);
	private PollerImpl poller;

	public AcceptEventHandler() throws IOException {
		poller = new PollerImpl();
	}

	@Override
	public void handle(SelectionKey selectionKey, Selector selector) {
		Multimap<Integer, SocketChannel> clients = ArrayListMultimap.create();
		try {
			LOGGER.info("Handle READ event");
			if (selectionKey.isAcceptable()) {
				final ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());
				final SocketChannel client = server.accept();
				client.configureBlocking(false);
				LOGGER.info("New connection accepted:" + client);
				clients.put(server.socket().getLocalPort(), client);
				poller.setClients(clients);

				client.register(selector, SelectionKey.OP_READ);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
