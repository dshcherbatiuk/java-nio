package com.nio.reactor.event;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptEventHandler implements EventHandler {
	private final Selector selector;

	public AcceptEventHandler(final Selector selector) {
		this.selector = selector;
	}

	@Override
	public void handle(final SelectionKey selectionKey) {
		System.out.println("Handle ACCEPT event");

		if (!selectionKey.isAcceptable()) {
			System.out.println("Channel is no acceptable! ");
			return;
		}

		try {
			final ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());
			final SocketChannel client = server.accept();
			client.configureBlocking(false);
			System.out.printf("New connection accepted: %s%n", client);

			selector.selectNow();
			client.register(selector, SelectionKey.OP_READ);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
