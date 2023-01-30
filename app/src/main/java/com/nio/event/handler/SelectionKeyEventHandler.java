package com.nio.event.handler;

import java.nio.channels.SelectionKey;

public interface SelectionKeyEventHandler {
    void handle(SelectionKey selectionKey);
}
