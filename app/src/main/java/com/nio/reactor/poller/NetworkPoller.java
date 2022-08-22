package com.nio.reactor.poller;

import com.nio.reactor.event.EventHandler;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;

@NotThreadSafe
public final class NetworkPoller implements Poller {

	private final HashMap<Integer, EventHandler> handlers;
	private final Selector selector;

	public NetworkPoller() throws IOException {
		this.selector = Selector.open();
		this.handlers = new HashMap<>();
	}

	public Selector getSelector() {
		return selector;
	}

	public void registerChannel(final int type, final SelectableChannel channel) throws ClosedChannelException {
		channel.register(selector, type);
	}

	public void registerEventhandler(final int type, final EventHandler eventHandler) {
		System.out.printf("Register event handler: type=%d handler=%s%n", type, eventHandler);
		this.handlers.put(type, eventHandler);
	}

	@Override
	public void poll() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				System.out.println("Wait new events..");
				selector.select();

				final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				iterator.forEachRemaining(selectionKey -> {
					try {
						final EventHandler eventHandler = handlers.get(selectionKey.readyOps());
						eventHandler.handle(selectionKey);
					} catch (Exception e) {
						e.printStackTrace();
					}
					iterator.remove();
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
