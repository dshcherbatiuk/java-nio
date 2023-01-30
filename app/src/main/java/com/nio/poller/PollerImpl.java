package com.nio.poller;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.nio.eventhadler.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PollerImpl implements Poller {
	private static final Logger LOGGER = LoggerFactory.getLogger(PollerImpl.class);
	private final Selector selector;
	private final Map<Integer, EventHandler> events;
	private Multimap<Integer, SocketChannel> clients;

	public PollerImpl() throws IOException {
		this.selector = Selector.open();
		this.events = new HashMap<>();
		this.clients = ArrayListMultimap.create();
	}

	@Override
	public void poll() {
		LOGGER.info("Wait new events ");

		while (!Thread.currentThread().isInterrupted()) {
			try {
				selector.select();
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				iterator.forEachRemaining(selectionKey -> {
					final EventHandler eventHandler = events.get(selectionKey.readyOps());
					eventHandler.handle(selectionKey, selector);
					iterator.remove();
				});
			} catch (IOException e) {
				LOGGER.info(e.getMessage());
			}
		}
	}

	public void registerChannel(SelectableChannel channel, int selectionKey) throws ClosedChannelException {
		channel.register(selector, selectionKey);
	}

	public void registerEvent(int selectionType, EventHandler eventHandler) {
		events.put(selectionType, eventHandler);
	}

	public Multimap<Integer, SocketChannel> getClients() {
		return clients;
	}

	public void setClients(Multimap<Integer, SocketChannel> clients) {
		this.clients = clients;
	}

	public void registerEvent(final Selector selector, final Multimap<Integer, SocketChannel> clients, final int op) {

		clients.get(5555).forEach(c -> {
			try {
				c.register(selector, op);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
