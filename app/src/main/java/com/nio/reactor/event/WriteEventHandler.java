package com.nio.reactor.event;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class WriteEventHandler implements EventHandler {
	private final Selector selector;

	public WriteEventHandler(final Selector selector) {
		this.selector = selector;
	}

	@Override
	public void handle(final SelectionKey selectionKey) {
		System.out.println("Handle WRITE event");

		if (!selectionKey.isWritable()) {
			System.out.println("Channel is no writable! ");
			return;
		}

		try {
			final SocketChannel client = (SocketChannel) selectionKey.channel();
			final ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();

			toUpperCase(buffer);

			client.write(buffer);
			buffer.clear();

			client.register(selector, SelectionKey.OP_READ, buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void toUpperCase(final ByteBuffer buffer) {
		for (int i = 0; i < buffer.limit(); i++) {
			buffer.put(i, (byte) Character.toUpperCase(buffer.get(i)));
		}
	}
}
