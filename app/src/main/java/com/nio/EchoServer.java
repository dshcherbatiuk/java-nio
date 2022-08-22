package com.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public final class EchoServer {

	public static final String HOSTNAME = "0.0.0.0";
	public static final int PORT = 5555;

	private EchoServer() {
	}

	public static void main(final String... args) throws Exception {
		System.out.printf("Echo Server started at %s:%s ", HOSTNAME, PORT);
		final Selector selector = Selector.open();

		final ServerSocketChannel serverSocket = ServerSocketChannel.open();
		serverSocket.bind(new InetSocketAddress(HOSTNAME, PORT));
		serverSocket.configureBlocking(false);
		serverSocket.register(selector, SelectionKey.OP_ACCEPT);

		final ByteBuffer buffer = ByteBuffer.allocate(65535);

		while (!Thread.currentThread().isInterrupted()) {
			System.out.println("Wait new events..");
			selector.select();

			final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			iterator.forEachRemaining(selectionKey -> {
				try {
					if (selectionKey.isAcceptable()) {
						System.out.println("Handle ACCEPT event");
						final SocketChannel client = serverSocket.accept();
						client.configureBlocking(false);
						System.out.printf("New connection accepted: %s%n", client);

						client.register(selector, SelectionKey.OP_READ);
					}

					if (selectionKey.isReadable()) {
						System.out.println("Handle READ event");
						final SocketChannel client = (SocketChannel) selectionKey.channel();
						final int read = client.read(buffer);

						if (read == -1) {
							client.close();
							client.keyFor(selector).cancel();
							System.out.printf("The connection was closed: %s%n", client);
							return;
						}

						buffer.flip();
						client.write(buffer);
						buffer.clear();
					}
				} catch (Exception e) {
					System.out.println(e);
				}
				iterator.remove();
			});
		}
	}
}
