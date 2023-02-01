package com.nio.example.event;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface EventHandler {

  boolean select(SelectionKey selectionKey);

  void handle(SelectionKey selectionKey) throws IOException;
}
