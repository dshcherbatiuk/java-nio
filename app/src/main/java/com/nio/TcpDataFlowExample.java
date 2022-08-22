package com.nio;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

public final class TcpDataFlowExample {

	public static final String HOSTNAME = "0.0.0.0";
	public static final int[] PORTS = new int[] { 5555, 4444 };

	private TcpDataFlowExample() {
	}

	public static void main(final String... args) throws Exception {
		System.out.printf("Tcp Data Flow Example started at %s:%s%n", HOSTNAME, Arrays.toString(PORTS));
		final Selector selector = Selector.open();

		for (final int port : PORTS) {
			final ServerSocketChannel serverSocket = ServerSocketChannel.open();
			serverSocket.bind(new InetSocketAddress(HOSTNAME, port));
			serverSocket.configureBlocking(false);
			serverSocket.register(selector, SelectionKey.OP_ACCEPT);
		}

		final ByteBuffer buffer = ByteBuffer.allocate(65535);
		final Multimap<Integer, SocketChannel> clients = ArrayListMultimap.create();

		while (!Thread.currentThread().isInterrupted()) {
			System.out.printf("Wait new events..%n");
			selector.select();

			final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			iterator.forEachRemaining(selectionKey -> {
				try {
					if (selectionKey.isAcceptable()) {
						System.out.println("Handle READ event");
						final ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());
						final SocketChannel client = server.accept();
						client.configureBlocking(false);
						System.out.printf("New connection accepted: %s%n", client);

						clients.put(server.socket().getLocalPort(), client);

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
						switch (client.socket().getLocalPort()) {
						case 5555:
							for (int i = 0; i < buffer.limit(); i++) {
								buffer.put(i, (byte) Character.toUpperCase(buffer.get(i)));
							}
							client.write(buffer);
							break;
						case 4444:
							final String command = StandardCharsets.UTF_8.decode(buffer).toString();
							final String stopCommand = "stop-read";
							if (stopCommand.equals(command.trim().toLowerCase())) {
								System.out.println("Handle stop-read");
								registerEvent(selector, clients, 0);
								break;
							}
							final String startCommand = "start-read";
							if (startCommand.equals(command.trim().toLowerCase())) {
								System.out.println("Handle start-read");
								registerEvent(selector, clients, SelectionKey.OP_READ);
								break;
							}

							final byte[] unknownCommand = String.format("Supported commands:%n%s%n%s%n", stopCommand, startCommand)
									.getBytes(StandardCharsets.UTF_8);
							client.write(ByteBuffer.wrap(unknownCommand));
							break;
						}

						buffer.clear();
					}
					iterator.remove();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

	private static void registerEvent(final Selector selector, final Multimap<Integer, SocketChannel> clients, final int op) {
		clients.get(5555).forEach(c -> {
			try {
				c.register(selector, op);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
