package com.nio.sample.handler;

import java.nio.channels.SelectionKey;

public interface EventHandler {

  void processEvent(SelectionKey selectionKey);
}