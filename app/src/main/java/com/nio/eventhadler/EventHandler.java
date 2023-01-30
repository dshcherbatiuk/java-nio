package com.nio.eventhadler;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public interface EventHandler {
	void handle(SelectionKey selectionKey, Selector selector);
}
