package com.nio.eventhadler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class WriteEventHandler implements EventHandler {
	@Override
	public void handle(SelectionKey selectionKey, Selector selector) {
		System.out.println("write event");
		try {
			final SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
			final ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
			int bytesRead = socketChannel.read(buffer);

			socketChannel.write(buffer);
			buffer.clear();

			if (bytesRead == -1) {
				System.out.println("connection closed " + socketChannel.getRemoteAddress());
				socketChannel.close();
			}
			socketChannel.register(selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
