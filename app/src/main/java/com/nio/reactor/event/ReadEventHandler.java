package com.nio.reactor.event;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ReadEventHandler implements EventHandler {
	private final Selector selector;
	private final ByteBuffer buffer;

	public ReadEventHandler(final Selector selector) {
		this.selector = selector;
		this.buffer = ByteBuffer.allocate(256);
	}

	@Override
	public void handle(final SelectionKey selectionKey) {
		System.out.println("Handle READ event");

		if (!selectionKey.isReadable()) {
			System.out.println("Channel is no readable! ");
			return;
		}

		try {
			final SocketChannel client = (SocketChannel) selectionKey.channel();
			final int read = client.read(buffer);

			if (read == -1) {
				// Client close connection
				client.close();
				client.keyFor(selector).cancel();
				System.out.printf("The connection was closed: %s%n", client);

				return;
			}

			buffer.flip();

			client.register(selector, SelectionKey.OP_WRITE, buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
