package com.nio.eventhadler;

import com.nio.poller.PollerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ReadEventHandler implements EventHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadEventHandler.class);
	private final ByteBuffer buffer = ByteBuffer.allocate(65536);
	private final PollerImpl poller = new PollerImpl();
	private final static String STOP_COMMAND = "stop-read";
	private final static String START_COMMAND = "start-read";

	public ReadEventHandler() throws IOException {
	}

	@Override
	public void handle(SelectionKey selectionKey, Selector selector) {
		try {
			if (selectionKey.isReadable()) {
				LOGGER.info("read event");
				final SocketChannel client = (SocketChannel) selectionKey.channel();
				final int read = client.read(buffer);

				if (read == -1) {
					client.close();
					client.keyFor(selector).cancel();
					LOGGER.info("The connection was closed: " + client);
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
						if (STOP_COMMAND.equalsIgnoreCase(command.trim())) {
							LOGGER.info("Handle stop-read");
							poller.registerEvent(selector, poller.getClients(), 0);
							break;
						}
						if (START_COMMAND.equalsIgnoreCase(command.trim())) {
							LOGGER.info("Handle start-read");
							poller.registerEvent(selector, poller.getClients(), SelectionKey.OP_READ);
							break;
						}

						final byte[] unknownCommand = String.format("Supported commands:%n%s%n%s%n", STOP_COMMAND, START_COMMAND)
								.getBytes(StandardCharsets.UTF_8);
						client.write(ByteBuffer.wrap(unknownCommand));
						break;
				}

				buffer.clear();
			}
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
		}
	}
}
