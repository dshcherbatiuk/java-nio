package com.nio.reactor.event;

import java.nio.channels.SelectionKey;

@FunctionalInterface
public interface EventHandler {

	void handle(final SelectionKey selectionKey);
}
